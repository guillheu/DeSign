import static org.junit.Assert.*;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import contractWrappers.DeSign;
import core.DeSignCore;
import core.DeSignCore.SignatureProof;
import kotlin.Pair;
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
	static String walletFilePath;
	static BigInteger gasPrice;
	static BigInteger gasLimit;
	static String SQLDBName;
	static String SQLTableName;
	static String SQLVolumeIDColumnName;
	static String SQLDataColumnName;
	static String SQLIdColumnName;
	static String localStorageRoot;
	static String externalNodeURL;
	static String defaultFilePath;
	static String sqlDriverClassName;
	
	
	
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
	static String indexVolume1 = "21/04/21";
	static String indexVolume2 = "24/04/22";
	static String linkDBVolume1;
	static String linkDBVolume2;
	static byte[] dataHash;
	static int amountOfLeaves = 7;
	static String secondUser = "0x74DCaFDc8591f44a1516b0CB2B979223b3c4fad1";


	
	
	
	static {
		try {
			config = (new Configurations()).properties(new File(configFilePath));
			
			
			//setting values from config file
			hashAlgo = 					config.getString("crypto.hashAlgo");
			localDBConnectionLink = 	config.getString("storage.SQLconnexionLink");
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
			localStorageRoot = 			config.getString("storage.localStorageRoot");
			externalNodeURL = 			config.getString("blockchain.nodeURLForExternalChecks");
			defaultFilePath = 			config.getString("documents.defaultPath");
			sqlDriverClassName = 		config.getString("storage.SQLDriver");
			
			
			
			
			
			

			Class.forName(sqlDriverClassName).getDeclaredConstructor().newInstance();
			gasProvider = new StaticGasProvider(gasPrice, gasLimit);
			creds = WalletUtils.loadCredentials("toto", walletFilePath);
			sha256 = MessageDigest.getInstance(hashAlgo);
			dataHash = sha256.digest(FileUtils.readFileToByteArray(new File(localStoragePath)));
			localStorage =  new TMPLocalFileStorage(localStorageRoot);
			SQLStorage =  new SQLStorage(sha256, localDBConnectionLink, SQLDBName, SQLTableName, SQLVolumeIDColumnName, SQLDataColumnName, SQLIdColumnName);
			coreLocalStorage = new DeSignCore(nodeURL, addr, creds, gasProvider, localStorage, sha256);
			coreSQLDB = new DeSignCore(nodeURL, addr, creds, gasProvider, SQLStorage, sha256);
			linkDBVolume1 = BytesUtils.bytesToHexString(sha256.digest(indexVolume1.getBytes()));
			linkDBVolume2 = BytesUtils.bytesToHexString(sha256.digest(indexVolume2.getBytes()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIndexation() {	
		System.out.println(dataHash);
		System.out.println("send document hash = " + BytesUtils.bytesToHexString(dataHash) + "\n" +
				"send expiration time = " + defaultValidityTime);
		
			try {
				DeSignCore core = new DeSignCore(nodeURL, creds, gasProvider, localStorage, sha256);
				core.signMerkleRoot(sha256.digest(indexVolume1.getBytes()),dataHash, defaultValidityTime);
				Tuple3<byte[], BigInteger, String> r = core.getIndexInfo(indexVolume1);
				System.out.println("received document hash = " + BytesUtils.bytesToHexString(r.component1()) + "\n" +
						"received expiration time = " + r.component2().divide(BigInteger.valueOf(86400)));
				
				
				assertEquals(BytesUtils.bytesToHexString(r.component1()), BytesUtils.bytesToHexString(dataHash));
				assertEquals(r.component2().intValue(), defaultValidityTime);
			} catch (Exception e) {
				System.out.println("lol ??");
				e.printStackTrace();
			}
	}
	
	
	@Test
	public void testIntegration() {
		try {
			System.out.println("\n\nINTEGRATION TEST\n");
			
			

			DeSignCore localDsc = new DeSignCore(nodeURL, creds, gasProvider, SQLStorage, sha256);
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
		DocumentVolumeStorage testStorage = new SQLStorage(sha256, localDBConnectionLink, SQLDBName, SQLTableName, SQLVolumeIDColumnName, SQLDataColumnName, SQLIdColumnName);
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
			List<Pair<String, String>> merklePath;
			merklePath = merkleTree.getMerklePath(sigs.get(0));
			assertTrue(sigs.get(1).equals(merklePath.get(0).component2()));
			String sigs2and3hashed = BytesUtils.bytesToHexString(sha256.digest(ArrayUtils.addAll(sha256.digest((""+2).getBytes()) , sha256.digest((""+3).getBytes()))));
			System.out.println(sigs2and3hashed + " should equal " + merklePath.get(1));
			assertTrue(sigs2and3hashed.equals(merklePath.get(1).component2()));
			String current = sigs.get(0);
			for(Pair<String, String> step : merklePath) {
				if(step.getFirst().equals("LEFT"))
					current = BytesUtils.bytesToHexString(sha256.digest(ArrayUtils.addAll(BytesUtils.hexStringToByteArray(current), BytesUtils.hexStringToByteArray(step.getSecond()))));
				else if(step.getFirst().equals("RIGHT"))
					current = BytesUtils.bytesToHexString(sha256.digest(ArrayUtils.addAll(BytesUtils.hexStringToByteArray(step.getSecond()), BytesUtils.hexStringToByteArray(current))));
					
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
		System.out.println("current, before any hashing : " + BytesUtils.bytesToHexString(current));
		
		try {
			for(Pair<String,String> step : sigProof.merklePath) {
				byte[] byteStep = BytesUtils.hexStringToByteArray(step.getSecond().substring(2));
				System.out.println("step : "+step);
				byte[] concatHashes = null;
				if(step.component1().equals("LEFT"))
					concatHashes = ArrayUtils.addAll(current, byteStep);
				else if(step.component1().equals("RIGHT"))
					concatHashes = ArrayUtils.addAll(byteStep, current);
				else
					throw new Exception("Unknown merkle path step direction : " + step.component1());
				System.out.println("Temporary concatenated hashes : " + BytesUtils.bytesToHexString(concatHashes));
				current = sha256.digest(concatHashes);
				System.out.println("current, after hashing with step : " + BytesUtils.bytesToHexString(current));
			}
			String foundRoot = BytesUtils.bytesToHexString(current);
			String expectedRoot = BytesUtils.bytesToHexString(coreSQLDB.getDocumentVolumeMerkleRoot(indexVolume1, sha256));
			System.out.println("Expected : " + expectedRoot);
			System.out.println("Found : " + foundRoot);
			assertTrue(expectedRoot.equals(foundRoot));
			
			//simulating outside check

			Web3j clientWeb3 = Web3j.build(new HttpService(sigProof.nodeURL));
			DeSign clientContract = DeSign.load(sigProof.contractAddr, clientWeb3, creds, gasProvider);
			byte[] foundIndexHash = BytesUtils.hexStringToByteArray(sigProof.indexHash.substring(2));
			Tuple3<byte[], BigInteger, String> output = clientContract.getIndexData(foundIndexHash).send();
			System.out.println("found on signature : " + foundRoot);
			System.out.println("found on chain : " + BytesUtils.bytesToHexString(output.component1()));
			assertTrue(BytesUtils.bytesToHexString(output.component1()).equals(foundRoot));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRoles(){
		try {
			assertTrue(coreSQLDB.isSignatory(creds.getAddress()));
			assertFalse(coreSQLDB.isSignatory(secondUser));
			assertTrue(coreSQLDB.isDefaultAdmin(creds.getAddress()));
			assertFalse(coreSQLDB.isDefaultAdmin(secondUser));
			coreSQLDB.makeSignatory(secondUser);
			assertTrue(coreSQLDB.isSignatory(creds.getAddress()));
			assertTrue(coreSQLDB.isSignatory(secondUser));
			assertTrue(coreSQLDB.isDefaultAdmin(creds.getAddress()));
			assertFalse(coreSQLDB.isDefaultAdmin(secondUser));
			coreSQLDB.revokeSignatory(secondUser);
			assertTrue(coreSQLDB.isSignatory(creds.getAddress()));
			assertFalse(coreSQLDB.isSignatory(secondUser));
			assertTrue(coreSQLDB.isDefaultAdmin(creds.getAddress()));
			assertFalse(coreSQLDB.isDefaultAdmin(secondUser));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
