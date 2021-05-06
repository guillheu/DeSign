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
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
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
	
	private static String hashAlgo;
	private static String addr;
	private static String nodeURL;
	private static DocumentVolumeStorage localStorage;
	private static DocumentVolumeStorage SQLStorage;
	private static String localDBConnectionLink;
	private static String localDBConnectionUsername;
	private static String walletFilePath;
	private static BigInteger gasPrice;
	private static BigInteger gasLimit;
	private static String SQLDBName;
	private static String SQLTableName;
	private static String SQLVolumeIDColumnName;
	private static String SQLDataColumnName;
	private static String SQLIdColumnName;
	private static String externalNodeURL;
	private static String defaultFilePath;
	private static String sqlDriverClassName;
	

	private static PropertiesConfiguration config;
	
	/*
	 * 
	 * Core components
	 * 
	 */
	
	private static MessageDigest sha256;
	private static Credentials creds;
	private static ContractGasProvider gasProvider;
	private static DeSignCore coreSQLDB;
	private static TransactionReceipt lastTransaction;
	
	
	public static void initFromWeb() throws Exception {
		String wltPwd = (new Configurations()).properties(new File("./wlt.pwd")).getString("pwd");
		String sqlPwd = (new Configurations()).properties(new File("./sql.pwd")).getString("pwd");
		init("./config.properties", wltPwd, sqlPwd);
	}
	
	
	public static void init(String configFilePath, String walletPwd, String sqlPwd) throws Exception {

		config = (new Configurations()).properties(new File(configFilePath));
		//setting values from config file
		hashAlgo = 					config.getString("crypto.hashAlgo");
		localDBConnectionLink = 	config.getString("storage.SQLconnexionLink");
		localDBConnectionUsername = config.getString("storage.SQLconnexionUser");
		walletFilePath = 			config.getString("blockchain.walletFile");
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
		System.out.println(walletFilePath);
		Class.forName(sqlDriverClassName).getDeclaredConstructor().newInstance();
		gasProvider = new StaticGasProvider(gasPrice, gasLimit);
		
		creds = WalletUtils.loadCredentials(walletPwd, walletFilePath);
		sha256 = MessageDigest.getInstance(hashAlgo);
		SQLStorage =  new SQLStorage(sha256, localDBConnectionLink, localDBConnectionUsername, sqlPwd, SQLDBName, SQLTableName, SQLVolumeIDColumnName, SQLDataColumnName, SQLIdColumnName);
		new DeSignCore(nodeURL, addr, creds, gasProvider, localStorage, sha256);
		coreSQLDB = new DeSignCore(nodeURL, addr, creds, gasProvider, SQLStorage, sha256);
	}
	
	public static String getAccountBalance() {
		try {
			return coreSQLDB.getAddressBalance(creds.getAddress()) + "ETH\n";
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error, try again later";
	}

	public static boolean signDocumentVolume(String index, int validityTime) {
		try {
			lastTransaction = coreSQLDB.sign(index, validityTime*86400);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkSignature(String index) {
		try {
			return coreSQLDB.checkSignature(index);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean exportSigProof(byte[] document, String path) {
		try {
			SignatureProof sigProof = coreSQLDB.getSignatureProof(document, externalNodeURL);
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
	
	public static boolean exportSigProof(int documentID, String path) {
		return exportSigProof(coreSQLDB.getDocumentFromID(documentID), path);
	}
	
	public static int importDocument(byte[] document, String index) {
		

		return coreSQLDB.indexDocumentIntoStorage(document, index);
		
	}
	
	
	
	public static void main(String args[]) {
		if(args.length >= 1) {
			try {

				System.out.println("PLEASE ENTER WALLET FILE PASSWORD : ");
				String wltPwd = String.valueOf(System.console().readPassword());
				System.out.println("PLEASE ENTER SQL DATABASE PASSWORD : ");
				String sqlPwd = String.valueOf(System.console().readPassword());
				DeSignAppLauncher.init(args[0], wltPwd, sqlPwd);
				wltPwd = sqlPwd = "";	//NOT COMPLETELY SECURE
			}
			catch(Exception e) {
				return;
			}
			while(true) {
				try {
					BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
					int action = 0;
					while(action != 1 && action != 2 && action != 3 && action != 4 && action != 5) {
						
						System.out.println("\nWelcome to DeSign\n\n"
								+ "node URL : " + nodeURL + "\n"
								+ "contract address : " + addr + "\n"
								+ "your address : " + creds.getAddress() + "\n"
								+ "SQL database connexion link : " + localDBConnectionLink + "\n\n"
								+ "\nYour balance : " + getAccountBalance()
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
						if(signDocumentVolume(index, secondsBeforeExpiration)) {
							System.out.println("Signature successful !");
							System.out.println(getLastTransactionInfo());
						}
						else {
							System.out.println("Transaction failed");
						}
						
					}
					else if(action == 2) {
						String index;
						System.out.println("What is the index to check ?");
						index = console.readLine();
						if(checkSignature(index)) {
							Tuple3<byte[], BigInteger, String> r = getIndexInfo(index);
							System.out.println("Signature matched documents - database integrity validated for this index");
							
							System.out.println("Signed by : " + r.component3());
							System.out.println("Valid for : " + r.component2().floatValue()/86400 + " days");
						}
						else {
							
						}
						
					}
					else if(action == 3) {
						String documentPath;
						String documentName;
						System.out.println("Using default path " + defaultFilePath);
						documentPath = defaultFilePath;
						System.out.println("What is the name of the document ?");
						documentName = console.readLine();
						byte[] document = FileUtils.readFileToByteArray(new File(documentPath+documentName));
						if(exportSigProof(document, documentPath)) {
							System.out.println("Proof of signature successfully exported at " + documentPath + "sigProof.json");
						}
						else {
							System.err.println("Proof of signature export failed! Is this document in the database ?");
						}
					}
					else if(action == 4) {
						String documentName;
						String documentPath = defaultFilePath;
						String index;
						System.out.println("What is the index ?");
						index = console.readLine();
						System.out.println("Using default path " + defaultFilePath);
						System.out.println("What is the name of the document ?");
						documentName = console.readLine();
						byte[] document = FileUtils.readFileToByteArray(new File(documentPath + documentName));
						
						System.out.println("Document successfully imported at index " + importDocument(document, index));
						
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
							if(isSignatory(address)) {
								System.out.println("this address is signatory !");
							}
							else {
								System.out.println("this address is not signatory");
							}
						}
						else if(action2 == 2) {
							if(isDefaultAdmin(address)) {
								System.out.println("this address is default admin !");
							}
							else {
								System.out.println("this address is not default admin");
							}
						}
						else if(action2 == 3) {
							makeSignatory(address);
							System.out.println(address + " Is now signatory!");
							System.out.println(getLastTransactionInfo());
						}
						else if(action2 == 4) {
							revokeSignatory(address);
							System.out.println(address + "Is no longer signatory!");
							System.out.println(getLastTransactionInfo());
						}
						
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
			}
		}
	}

	public static boolean isSignatory(String address) throws Exception {
		return coreSQLDB.isSignatory(address);
	}

	public static boolean isDefaultAdmin(String address) throws Exception {
		return coreSQLDB.isDefaultAdmin(address);
	}

	public static void makeSignatory(String address) throws Exception {
		lastTransaction = coreSQLDB.makeSignatory(address);
	}

	public static void revokeSignatory(String address) throws Exception {
		lastTransaction = coreSQLDB.revokeSignatory(address);
	}
	
	public static String getHashAlgo() {
		return hashAlgo;
	}

	public static String getContractAddress() {
		return addr;
	}

	public static String getNodeURL() {
		return nodeURL;
	}

	public static BigInteger getGasPrice() {
		return gasPrice;
	}

	public static BigInteger getGasLimit() {
		return gasLimit;
	}

	public static String getExternalNodeURL() {
		return externalNodeURL;
	}

	public static String getDefaultFilePath() {
		return defaultFilePath;
	}
	
	public static String getUserAddress() {
		return creds.getAddress();
	}
	
	public static String getUserBalance() {
		try {
			return coreSQLDB.getAddressBalance(addr).toPlainString();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "THERE WAS AN ERROR";
	}


	public static TransactionReceipt getLastTransaction() {
		return lastTransaction;
	}
	
	public static Tuple3<byte[], BigInteger, String> getIndexInfo(String index) {
		try {
			return coreSQLDB.getIndexInfo(index);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static int getNetworkID() {
		return coreSQLDB.getNetworkID();
	}
	
	private static String getLastTransactionInfo() {
		String r = "";
		r += "Transaction hash : " + lastTransaction.getTransactionHash();
		r += "\nBlock number : " + lastTransaction.getBlockNumber();
		r += "\nGas used : " + lastTransaction.getCumulativeGasUsed();
		return r;
	}
}
