import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import contractWrappers.DeSign;
import core.DeSignCore;
import core.DeSignCore.SignatureProof;
import storage.DocumentVolumeStorage;
import storage.SQLStorage;
import storage.TMPLocalFileStorage;
import util.BytesUtils;
import util.MerkleTree;

public class DeSign_test {
	static Configuration config;

	/*
	 * 
	 * Core config variables
	 * 
	 */
	
	static String hashAlgo;
	static String addr;
	static String nodeURL;
	static DocumentVolumeStorage localStorage;
	static DocumentVolumeStorage SQLStorage;
	static int defaultValidityTime;
	static String localDBConnectionLink;
	static String privateKey;
	static BigInteger gasPrice;
	static BigInteger gasLimit;
	static String SQLDBName;
	static String SQLTableName;
	static String SQLVolumeIDColumnName;
	static String SQLDataColumnName;
	static String localStorageRoot;
	
	
	
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
	
	/*
	 * 
	 * Arbitraty test values
	 * 
	 */

	static String configFilePath = "src/test/resources/testConfig.properties";
	static String localStoragePath = "src/test/resources/Document";
	static String indexVolume1 = "A girl with a short skirt and a long jacket";
	static String indexVolume2 = "Somebody to love";
	static String linkDBVolume1;
	static String linkDBVolume2;
	static byte[] dataHash;
	static int amountOfLeaves = 7;

	
	
	
	static {
		try {
			config = (new Configurations()).properties(new File(configFilePath));
			
			
			//setting values from config file
			hashAlgo = 					config.getString("crypto.hashAlgo");
			localDBConnectionLink = 	config.getString("storage.SQLconnexionLink");
			privateKey = 				config.getString("blockchain.privKey");
			addr = 						config.getString("blockchain.contractAddr");
			nodeURL = 					config.getString("blockchain.nodeURL");
			defaultValidityTime = 		config.getInt("documents.defaultValiditytime");
			gasPrice = 					new BigInteger(config.getString("blockchain.gasPrice"));
			gasLimit = 					new BigInteger(config.getString("blockchain.gasLimit"));
			SQLDBName = 				config.getString("storage.SQLDBName");
			SQLTableName = 				config.getString("storage.SQLTableName");
			SQLVolumeIDColumnName = 	config.getString("storage.SQLVolumeIDColumnName");
			SQLDataColumnName = 		config.getString("storage.SQLDataColumnName");
			localStorageRoot = 			config.getString("storage.localStorageRoot");
			
			
			

			Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
			gasProvider = new StaticGasProvider(gasPrice, gasLimit);
			creds = Credentials.create(privateKey);
			sha256 = MessageDigest.getInstance(hashAlgo);
			dataHash = sha256.digest(FileUtils.readFileToByteArray(new File(localStoragePath)));
			localStorage =  new TMPLocalFileStorage(localStorageRoot);
			SQLStorage =  new SQLStorage(sha256, localDBConnectionLink, SQLDBName, SQLTableName, SQLVolumeIDColumnName, SQLDataColumnName);
			coreLocalStorage = new DeSignCore(nodeURL, addr, creds, gasProvider, localStorage, sha256);
			coreSQLDB = new DeSignCore(nodeURL, addr, creds, gasProvider, SQLStorage, sha256);
			linkDBVolume1 = BytesUtils.bytesToHexString(sha256.digest(indexVolume1.getBytes()));
			linkDBVolume2 = BytesUtils.bytesToHexString(sha256.digest(indexVolume2.getBytes()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	

	public static void main(String args[]) {
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		int action = 0;
		try {
			while(action != 1 && action != 2) {
				System.out.println("Welcome to DeSign\n\n"
						+ "node URL : " + nodeURL + "\n"
						+ "contract address : " + addr + "\n"
						+ "SQL database connexion link : " + localDBConnectionLink + "\n\n"
						+ "What do you want to do ?\n"
						+ "1) Sign a document volume\n"
						+ "2) Check a stored signature"
						);
				try {
					action = Integer.parseInt(console.readLine());
				} catch (Exception e) {
					System.err.println("please enter a valid number");
				}
			}
			if(action == 1) {
				String index;
				int daysBeforeExpiration;
				System.out.println("What is the index ?");
				index = console.readLine();
				System.out.println("How many seconds before documents expiration ?");
				daysBeforeExpiration = Integer.parseInt(console.readLine());
				
				try {
					coreSQLDB.sign(index, daysBeforeExpiration);
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
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	
	@Test
	public void testIndexation() {		
		System.out.println("send document hash = " + BytesUtils.bytesToHexString(dataHash) + "\n" +
				"send expiration time = " + defaultValidityTime);
		
			try {
				coreLocalStorage.signMerkleRoot(sha256.digest(indexVolume1.getBytes()),dataHash, defaultValidityTime);
			} catch (Exception e) {
				System.out.println("lol ??");
				e.printStackTrace();
			}

			try {
				Tuple2<byte[], BigInteger> r = coreLocalStorage.getIndexInfo(indexVolume1);
				System.out.println("received document hash = " + BytesUtils.bytesToHexString(r.component1()) + "\n" +
						"received expiration time = " + r.component2().divide(BigInteger.valueOf(86400)));
				
				
				assertEquals(BytesUtils.bytesToHexString(r.component1()), BytesUtils.bytesToHexString(dataHash));
				assertEquals(r.component2().intValue(), defaultValidityTime);
			} catch (Exception e) {
				//e.printStackTrace();
			}
	}
	
	@Test
	public void testMerkleStorage() {
		try {
			System.out.println("volume1 root : " +  BytesUtils.bytesToHexString(localStorage.getIndexedDocumentVolumeMerkleRoot(indexVolume1, sha256)) + "\n" + 
			"volume2 root : " + BytesUtils.bytesToHexString(localStorage.getIndexedDocumentVolumeMerkleRoot(indexVolume2, sha256)));
			assertNotEquals(BytesUtils.bytesToHexString(localStorage.getIndexedDocumentVolumeMerkleRoot(indexVolume1, sha256)), BytesUtils.bytesToHexString(localStorage.getIndexedDocumentVolumeMerkleRoot(indexVolume2, sha256)));
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIntegration() {
		try {
			System.out.println("\n\nINTEGRATION TEST\n");
			DeSignCore localDsc = new DeSignCore(nodeURL, creds, gasProvider, localStorage, sha256);
			System.out.println("smart contract redeployed at address " + coreLocalStorage.getContractAddress());
			fullCycle(indexVolume1, defaultValidityTime, localDsc);
			fullCycle(indexVolume2, defaultValidityTime, localDsc);
			fullCycle(indexVolume1, defaultValidityTime, localDsc);
			fullCycle(indexVolume2, defaultValidityTime, localDsc);
			
			

			localDsc = new DeSignCore(nodeURL, creds, gasProvider, SQLStorage, sha256);
			fullCycle(indexVolume1, defaultValidityTime, localDsc);
			fullCycle(indexVolume2, defaultValidityTime, localDsc);
			fullCycle(indexVolume1, defaultValidityTime, localDsc);
			fullCycle(indexVolume2, defaultValidityTime, localDsc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void fullCycle(String index, int lifetime, DeSignCore core) throws Exception {
		System.out.println("*****************************************************\n"+
				   "********************START OF CYCLE*******************\n"+
				   "*****************************************************\n\n"+
				   "Using : \n"+
				   "\tindex : " + index + "\n\n" +
				   "Fetching merkle root from storage...\n");
		

		
		
		
		String merkleRootStorage = BytesUtils.bytesToHexString(core.getDocumentVolumeMerkleRoot(index, sha256));
		System.out.println("Found merkle root : " + merkleRootStorage);
		System.out.println("\nSigning & indexing the merkle root on the smart contract");
		
		core.sign(index, lifetime);
		
		String merkleRootSC = BytesUtils.bytesToHexString(core.getIndexInfo(index).component1());
		
		System.out.println("Fetched merkle root from smart contract : " + merkleRootSC);
		System.out.println("Checking signatures...");
		assertEquals(merkleRootStorage, merkleRootSC);
		
		boolean check = core.checkSignature(index);
		assertTrue(check);
		if(check)
			System.err.println("Success !\n");
		else
			System.err.println("Failure !\n");
		
		
	}
	
	@Test
	public void testMySQLStorage() {
		DocumentVolumeStorage testStorage = new SQLStorage(sha256, localDBConnectionLink, SQLDBName, SQLTableName, SQLVolumeIDColumnName, SQLDataColumnName);
		try {
			byte[] res = testStorage.getIndexedDocumentVolumeMerkleRoot(indexVolume1, sha256);
			System.out.println(BytesUtils.bytesToHexString(res));
			assertTrue(BytesUtils.bytesToHexString(res).equals(BytesUtils.bytesToHexString(localStorage.getIndexedDocumentVolumeMerkleRoot(indexVolume1, sha256))));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMerklePathGenerator() {
		List<String> sigs = new ArrayList<String>();
		System.out.println("\nleaves : ");
		for(int i = 0; i < amountOfLeaves; i++ ) {
			String hash = BytesUtils.bytesToHexString(sha256.digest(("" + i).getBytes()));
			sigs.add(hash);
			System.out.println(hash);
		}
		
		
		try {
			MerkleTree merkleTree = new MerkleTree(sigs, sha256);
			System.out.println("\nroot : " + BytesUtils.bytesToHexString(merkleTree.getRoot().sig));
			List<String> merklePath;
			merklePath = merkleTree.getMerklePath(sigs.get(0));
			assertTrue(sigs.get(1).equals(merklePath.get(0)));
			String sigs2and3hashed = BytesUtils.bytesToHexString(sha256.digest(ArrayUtils.addAll(sha256.digest((""+2).getBytes()) , sha256.digest((""+3).getBytes()))));
			System.out.println(sigs2and3hashed + " should equal " + merklePath.get(1));
			assertTrue(sigs2and3hashed.equals(merklePath.get(1)));
			String current = sigs.get(0);
			for(String step : merklePath) {
				current = BytesUtils.bytesToHexString(sha256.digest(ArrayUtils.addAll(BytesUtils.hexStringToByteArray(current), BytesUtils.hexStringToByteArray(step))));
			}
			assertTrue(BytesUtils.bytesToHexString(merkleTree.getRoot().sig).equals(current));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenerateSignatureProof() {
		byte[] document = "This is document 1".getBytes();
		byte[] current = sha256.digest(document);
		SignatureProof sigProof = coreSQLDB.getSignatureProof(document, nodeURL);
		for(byte[] step : sigProof.merklePath) {
			current = sha256.digest(ArrayUtils.addAll(current, step));
		}
		String foundRoot = BytesUtils.bytesToHexString(current);
		try {
			String expectedRoot = BytesUtils.bytesToHexString(coreSQLDB.getDocumentVolumeMerkleRoot(indexVolume1, sha256));
			assertTrue(expectedRoot.equals(foundRoot));
			
			//simulating outside check

			Web3j clientWeb3 = Web3j.build(new HttpService(sigProof.nodeURL));
			DeSign clientContract = DeSign.load(sigProof.contractAddress, clientWeb3, creds, gasProvider);
			byte[] foundIndexHash = sigProof.indexHash;
			Tuple2<byte[], BigInteger> output = clientContract.getIndexData(foundIndexHash).send();
			System.out.println("");
			assertTrue(BytesUtils.bytesToHexString(output.component1()).equals(foundRoot));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
