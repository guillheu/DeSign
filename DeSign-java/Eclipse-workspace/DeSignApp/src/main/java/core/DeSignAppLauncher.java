package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

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
		externalNodeURL = 			config.getString("blockchain.nodeURLForExternalChecks");
		defaultFilePath = 			config.getString("documents.defaultPath");
		sqlDriverClassName = 		config.getString("storage.SQLDriver");
		
		Class.forName(sqlDriverClassName).getDeclaredConstructor().newInstance();
		gasProvider = new StaticGasProvider(gasPrice, gasLimit);
		creds = Credentials.create(privateKey);
		sha256 = MessageDigest.getInstance(hashAlgo);
		SQLStorage =  new SQLStorage(sha256, localDBConnectionLink, SQLDBName, SQLTableName, SQLVolumeIDColumnName, SQLDataColumnName);
		coreLocalStorage = new DeSignCore(nodeURL, addr, creds, gasProvider, localStorage, sha256);
		coreSQLDB = new DeSignCore(nodeURL, addr, creds, gasProvider, SQLStorage, sha256);
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
					while(action != 1 && action != 2 && action != 3 && action != 4) {
						System.out.println("Welcome to DeSign\n\n"
								+ "node URL : " + launcher.nodeURL + "\n"
								+ "contract address : " + launcher.addr + "\n"
								+ "your address : " + launcher.creds.getAddress() + "\n"
								+ "SQL database connexion link : " + launcher.localDBConnectionLink + "\n\n"
								+ "What do you want to do ?\n"
								+ "1) Sign a document volume\n"
								+ "2) Check a stored signature\n"
								+ "3) Export a document's signature proof\n"
								+ "4) Index a document into the SQL database\n"
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
						secondsBeforeExpiration = Integer.parseInt(console.readLine())*86400;
						
						try {
							coreSQLDB.sign(index, secondsBeforeExpiration);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(action == 2) {
						String index;
						System.out.println("What is the index to check ?");
						index = console.readLine();
						try {
							if(coreSQLDB.checkSignature(index)) {
								System.out.println("Signature matched documents !");
							}
							else {
								System.out.println("Signatures did not match");
							}
						} catch (Exception e) {
							System.err.println("Something went wrong");
							e.printStackTrace();
						}
					}
					else if(action == 3) {
						String documentPath;
						String documentName;
						System.out.println("Using default path " + launcher.defaultFilePath);
						documentPath = launcher.defaultFilePath;
						System.out.println("What is the name of the document ?");
						documentName = console.readLine();
						byte[] document = FileUtils.readFileToByteArray(new File(documentPath+documentName));
						SignatureProof sigProof = coreSQLDB.getSignatureProof(document, launcher.externalNodeURL);
						Gson gson = new Gson();
						String json = gson.toJson(sigProof);
						json = json.replace("first", "hashFrom");
						json = json.replace("second", "hashWith");
						Files.writeString(Paths.get(documentPath + "sigProof.json"), json, StandardCharsets.UTF_8);
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
						coreSQLDB.indexDocumentIntoStorage(document, index);
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		
				try {
					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
					
			}
		}
	}
	
}
