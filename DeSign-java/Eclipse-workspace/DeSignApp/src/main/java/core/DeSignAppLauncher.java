package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.concurrent.ExecutionException;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.io.FileUtils;
import org.web3j.crypto.Credentials;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import com.google.gson.Gson;

import core.DeSignCore.SignatureProof;

import storage.DocumentVolumeStorage;
import storage.SQLStorage;

public class DeSignAppLauncher {
	/*
	 * 
	 * Core config variables
	 * 
	 */
	
	private String hashAlgo;
	private String addr;
	private String nodeURL;
	private DocumentVolumeStorage localStorage;
	private DocumentVolumeStorage SQLStorage;
	private String localDBConnectionLink;
	private String privateKey;
	private BigInteger gasPrice;
	private BigInteger gasLimit;
	private String SQLDBName;
	private String SQLTableName;
	private String SQLVolumeIDColumnName;
	private String SQLDataColumnName;
	private String SQLIdColumnName;
	private String externalNodeURL;
	private String defaultFilePath;
	private String sqlDriverClassName;
	

	private PropertiesConfiguration config;
	
	/*
	 * 
	 * Core components
	 * 
	 */
	
	static MessageDigest sha256;
	static Credentials creds;
	static ContractGasProvider gasProvider;
	static String SQLLinkTemplate;
	static DeSignCore coreLocalStorage;
	static DeSignCore coreSQLDB;
	
	
	
	
	
	public DeSignAppLauncher(String configFilePath) throws Exception {
		
		config = (new Configurations()).properties(new File(configFilePath));
		//setting values from config file
		hashAlgo = 					config.getString("crypto.hashAlgo");
		localDBConnectionLink = 	config.getString("storage.SQLconnexionLink");
		privateKey = 				config.getString("blockchain.privKey");
		addr = 						config.getString("blockchain.contractAddr");
		nodeURL = 					config.getString("blockchain.nodeURL");
		gasPrice = 					new BigInteger(config.getString("blockchain.gasPrice"));
		gasLimit = 					new BigInteger(config.getString("blockchain.gasLimit"));
		SQLDBName = 				config.getString("storage.SQLDBName");
		SQLTableName = 				config.getString("storage.SQLTableName");
		SQLVolumeIDColumnName = 	config.getString("storage.SQLVolumeIDColumnName");
		SQLDataColumnName = 		config.getString("storage.SQLDataColumnName");
		SQLIdColumnName = 			config.getString("storage.idColumnName");
		externalNodeURL = 			config.getString("blockchain.nodeURLForExternalChecks");
		defaultFilePath = 			config.getString("documents.defaultPath");
		sqlDriverClassName = 		config.getString("storage.SQLDriver");
		
		Class.forName(sqlDriverClassName).getDeclaredConstructor().newInstance();
		gasProvider = new StaticGasProvider(gasPrice, gasLimit);
		creds = Credentials.create(privateKey);
		sha256 = MessageDigest.getInstance(hashAlgo);
		SQLStorage =  new SQLStorage(sha256, localDBConnectionLink, SQLDBName, SQLTableName, SQLVolumeIDColumnName, SQLDataColumnName, SQLIdColumnName);
		coreLocalStorage = new DeSignCore(nodeURL, addr, creds, gasProvider, localStorage, sha256);
		coreSQLDB = new DeSignCore(nodeURL, addr, creds, gasProvider, SQLStorage, sha256);
	}
	
	public String getAccountBalance() {
		try {
			return coreSQLDB.getAddressBalance(creds.getAddress()) + "ETH\n";
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error, try again later";
	}

	public boolean signDocumentVolume(String index, int validityTime) {
		try {
			coreSQLDB.sign(index, validityTime*86400);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public String checkSignature(String index) {
		try {
			if(coreSQLDB.checkSignature(index)) {
				return "Signature matched documents !\n";
			}
			else {
				return "Signatures did not match - Check database integrity\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Something went wrong";
		}
	}
	
	public boolean exportSigProof(byte[] document, String path) {
		try {
			SignatureProof sigProof = coreSQLDB.getSignatureProof(document, this.externalNodeURL);
			Gson gson = new Gson();
			String json = gson.toJson(sigProof);
			json = json.replace("first", "hashFrom");
			json = json.replace("second", "hashWith");
			Files.writeString(Paths.get(path + "sigProof.json"), json, StandardCharsets.UTF_8);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean exportSigProof(int documentID, String path) {
		return exportSigProof(coreSQLDB.getDocumentFromID(documentID), path);
	}
	
	public boolean importDocument(byte[] document, String index) {
		

		coreSQLDB.indexDocumentIntoStorage(document, index);
		
		return true;
	}
	
	
	
	public static void main(String args[]) {
		if(args.length >= 1) {
			DeSignAppLauncher launcher;
			try {

				launcher = new DeSignAppLauncher(args[0]);
			}
			catch(Exception e) {
				return;
			}
			while(true) {
				try {
					BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
					int action = 0;
					while(action != 1 && action != 2 && action != 3 && action != 4 && action != 5) {
						
						System.out.println("Welcome to DeSign\n\n"
								+ "node URL : " + launcher.nodeURL + "\n"
								+ "contract address : " + launcher.addr + "\n"
								+ "your address : " + launcher.creds.getAddress() + "\n"
								+ "SQL database connexion link : " + launcher.localDBConnectionLink + "\n\n"
								+ "\nYour balance : " + launcher.getAccountBalance()
								+ "\nWhat do you want to do ?\n"
								+ "1) Sign a document volume\n"
								+ "2) Check a stored signature\n"
								+ "3) Export a document's signature proof\n"
								+ "4) Index a document into the SQL database (DO NOT USE FOR PRODUCTION)\n"
								+ "5) Role management\n"
								);
						try {
							action = Integer.parseInt(console.readLine());
						} catch (Exception e) {
							System.err.println("please enter a valid number");
						}
					}
					if(action == 1) {
						String index;
						int secondsBeforeExpiration;
						System.out.println("What is the index ?");
						index = console.readLine();
						System.out.println("How many days before documents expiration ?");
						secondsBeforeExpiration = Integer.parseInt(console.readLine());
						launcher.signDocumentVolume(index, secondsBeforeExpiration);
						
					}
					else if(action == 2) {
						String index;
						System.out.println("What is the index to check ?");
						index = console.readLine();
						System.out.println(launcher.checkSignature(index));
						
					}
					else if(action == 3) {
						String documentPath;
						String documentName;
						System.out.println("Using default path " + launcher.defaultFilePath);
						documentPath = launcher.defaultFilePath;
						System.out.println("What is the name of the document ?");
						documentName = console.readLine();
						byte[] document = FileUtils.readFileToByteArray(new File(documentPath+documentName));
						launcher.exportSigProof(document, documentPath);
					}
					else if(action == 4) {
						String documentName;
						String documentPath = launcher.defaultFilePath;
						String index;
						System.out.println("What is the index ?");
						index = console.readLine();
						System.out.println("Using default path " + launcher.defaultFilePath);
						System.out.println("What is the name of the document ?");
						documentName = console.readLine();
						byte[] document = FileUtils.readFileToByteArray(new File(documentPath + documentName));
						launcher.importDocument(document, index);
					}
					else if(action == 5) {
						int action2 = 0;
						String address;
						System.out.println("What action to take?\n\n"
								+ "1) Check if an address has the signatory role\n"
								+ "2) Check if an address has the default admin role\n"
								+ "3) Grant an address the signatory role (requires default admin role)\n"
								+ "4) Revoke the signatory role from an address (requires default admin role)\n"
								);
						try {
							action2 = Integer.parseInt(console.readLine());
						} catch (Exception e) {
							System.err.println("please enter a valid number");
						}
						System.out.println("Please enter the address :");
						address = console.readLine();
						if(action2 == 1) {
							if(launcher.isSignatory(address)) {
								System.out.println("this address is signatory !");
							}
							else {
								System.out.println("this address is not signatory");
							}
						}
						else if(action2 == 2) {
							if(launcher.isDefaultAdmin(address)) {
								System.out.println("this address is default admin !");
							}
							else {
								System.out.println("this address is not default admin");
							}
						}
						else if(action2 == 3) {
							launcher.makeSignatory(address);
							System.out.println("Ok !");
						}
						else if(action2 == 4) {
							launcher.revokeSignatory(address);
							System.out.println("Ok !");
						}
						
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
			}
		}
	}

	public boolean isSignatory(String address) throws Exception {
		return coreSQLDB.isSignatory(address);
	}

	public boolean isDefaultAdmin(String address) throws Exception {
		return coreSQLDB.isDefaultAdmin(address);
	}

	public void makeSignatory(String address) throws Exception {
		coreSQLDB.makeSignatory(address);
	}

	public void revokeSignatory(String address) throws Exception {
		coreSQLDB.revokeSignatory(address);
	}
	
	public String getHashAlgo() {
		return hashAlgo;
	}

	public String getContractAddress() {
		return addr;
	}

	public String getNodeURL() {
		return nodeURL;
	}

	public BigInteger getGasPrice() {
		return gasPrice;
	}

	public BigInteger getGasLimit() {
		return gasLimit;
	}

	public String getExternalNodeURL() {
		return externalNodeURL;
	}

	public String getDefaultFilePath() {
		return defaultFilePath;
	}
	
	public String getUserAddress() {
		return creds.getAddress();
	}
	
	public String getUserBalance() {
		try {
			return coreSQLDB.getAddressBalance(addr).toPlainString();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "THERE WAS AN ERROR";
	}
}
