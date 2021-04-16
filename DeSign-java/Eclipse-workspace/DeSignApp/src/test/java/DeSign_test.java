import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.DefaultGasProvider;

import contractWrappers.DeSign;
import core.DeSignCore;
import storage.DocumentVolumeStorage;
import storage.SQLStorage;
import storage.TMPLocalFileStorage;

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
	
	/*
	 * 
	 * Core components
	 * 
	 */
	
	static MessageDigest sha256;
	static Credentials creds;
	static DeSignCore dsc;
	
	/*
	 * 
	 * Arbitraty test values
	 * 
	 */

	static String localStoragePath = "src/test/resources/Document";
	static String linkDBVolume1 = "test.Documents:1:data";
	static String linkDBVolume2 = "test.Documents:2:data";
	static int amountOfDocumentsTest = 4;
	static String indexVolume1 = "A girl with a short skirt and a long jacket";
	static String indexVolume2 = "Somebody to love";
	static String linkLocalVolume1 = "src/test/resources/DocumentVolume1/";
	static String linkLocalVolume2 = "src/test/resources/DocumentVolume2/";
	static byte[] dataHash;
	
	
	
	static {
		try {
			config = (new Configurations()).properties(new File("src/test/resources/testConfig.properties"));
			
			hashAlgo = config.getString("crypto.hashAlgo");
			localDBConnectionLink = config.getString("storage.SQLconnexionLink");
			privateKey = config.getString("blockchain.privKey");
			//localStoragePath = config.getString("storage.localPath");
			addr = config.getString("blockchain.contractAddr");
			nodeURL = config.getString("blockchain.nodeURL");
			defaultValidityTime = config.getInt("documents.defaultValiditytime");
			
			
			System.out.println(localStoragePath);
			System.out.println(localDBConnectionLink);
			

			Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
			creds = Credentials.create(privateKey);
			sha256 = MessageDigest.getInstance(hashAlgo);
			dataHash = sha256.digest(FileUtils.readFileToByteArray(new File(localStoragePath)));
			localStorage =  new TMPLocalFileStorage(sha256);
			SQLStorage =  new SQLStorage(sha256, localDBConnectionLink);
			dsc = new DeSignCore(nodeURL, addr, creds, localStorage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	

	public static void main(String args[]) {
		dsc.setStorage(SQLStorage);
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		int action = 0;
		try {
			while(action != 1 && action != 2) {
				System.out.println("Welcome to DeSign\n\n"
						+ "node URL : " + nodeURL + "\n"
						+ "contract address : " + addr + "\n"
						+ "SQL database connexion link : " + localDBConnectionLink + "\n"
						+ "Volumes found : " + 2 + "\n\n"
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
				String volumeLink;
				String index;
				int daysBeforeExpiration;
				System.out.println("What is the document volume link ?");
				volumeLink = console.readLine();
				System.out.println("What is the index ?");
				index = console.readLine();
				System.out.println("How many days before documents expiration ?");
				daysBeforeExpiration = Integer.parseInt(console.readLine());
				
				try {
					dsc.sign(index, volumeLink, daysBeforeExpiration);
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
					if(dsc.checkSignature(index)) {
						System.out.println("Signature matched documents !");
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
		System.out.println("send document hash = " + DeSignCore.bytesToHexString(dataHash) + "\n" +
				"send link = " + linkLocalVolume1 + "\n" +
				"send expiration time = " + defaultValidityTime);
		
			try {
				dsc.signMerkleRoot(sha256.digest(indexVolume1.getBytes()),dataHash, linkLocalVolume1.getBytes(), defaultValidityTime);
			} catch (Exception e) {
				System.out.println("lol ??");
				e.printStackTrace();
			}

			try {
				Tuple3<byte[], byte[], BigInteger> r = dsc.getIndexInfo(indexVolume1);
				System.out.println("received document hash = " + DeSignCore.bytesToHexString(r.component1()) + "\n" +
						"received link = " + new String(r.component2(), StandardCharsets.UTF_8) + "\n" +
						"received expiration time = " + r.component3().divide(BigInteger.valueOf(86400)));
				
				
				assertEquals(DeSignCore.bytesToHexString(r.component1()), DeSignCore.bytesToHexString(dataHash));
				assertEquals(new String(r.component2(), StandardCharsets.UTF_8), linkLocalVolume1);
				assertEquals(r.component3().divide(BigInteger.valueOf(86400)).intValue(), defaultValidityTime);
			} catch (Exception e) {
				//e.printStackTrace();
			}
	}
	
	@Test
	public void testMerkleStorage() {
		try {
			System.out.println("volume1 root : " +  DeSignCore.bytesToHexString(localStorage.getDocumentVolumeMerkleRoot(linkLocalVolume1)) + "\n" + 
			"volume2 root : " + DeSignCore.bytesToHexString(localStorage.getDocumentVolumeMerkleRoot(linkLocalVolume2)));
			assertNotEquals(DeSignCore.bytesToHexString(localStorage.getDocumentVolumeMerkleRoot(linkLocalVolume1)), DeSignCore.bytesToHexString(localStorage.getDocumentVolumeMerkleRoot(linkLocalVolume2)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIntegration() {
		try {
			System.out.println("\n\nINTEGRATION TEST\n");
			DeSignCore localDsc = new DeSignCore(nodeURL, DeSign.deploy(dsc.getWeb3(), creds, new DefaultGasProvider()).send().getContractAddress(), creds, localStorage);
			System.out.println("smart contract redeployed at address " + dsc.getContract().getContractAddress());
			fullCycle(indexVolume1, linkLocalVolume1, defaultValidityTime, localDsc);
			fullCycle(indexVolume2, linkLocalVolume2, defaultValidityTime, localDsc);
			fullCycle(indexVolume1, linkLocalVolume2, defaultValidityTime, localDsc);
			fullCycle(indexVolume2, linkLocalVolume1, defaultValidityTime, localDsc);
			
			

			localDsc = new DeSignCore(nodeURL, DeSign.deploy(dsc.getWeb3(), creds, new DefaultGasProvider()).send().getContractAddress(), creds, SQLStorage);
			fullCycle(indexVolume1, linkDBVolume1, defaultValidityTime, localDsc);
			fullCycle(indexVolume2, linkDBVolume1, defaultValidityTime, localDsc);
			fullCycle(indexVolume1, linkDBVolume2, defaultValidityTime, localDsc);
			fullCycle(indexVolume2, linkDBVolume2, defaultValidityTime, localDsc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void fullCycle(String index, String link, int lifetime, DeSignCore core) throws Exception {
		System.out.println("*****************************************************\n"+
				   "********************START OF CYCLE*******************\n"+
				   "*****************************************************\n\n"+
				   "Using : \n"+
				   "\tindex : " + index + "\n" +
				   "\tlink : " + link + "\n\n" +
				   "Fetching merkle root from storage...\n");
		

		
		
		
		String merkleRootStorage = DeSignCore.bytesToHexString(core.getStorage().getDocumentVolumeMerkleRoot(link));
		System.out.println("Found merkle root : " + merkleRootStorage);
		System.out.println("\nSigning & indexing the merkle root on the smart contract");
		
		core.sign(index, link, lifetime);
		
		String merkleRootSC = DeSignCore.bytesToHexString(core.getIndexInfo(index).component1());
		
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
		DocumentVolumeStorage testStorage = new SQLStorage(sha256, localDBConnectionLink);
		try {
			byte[] res = testStorage.getDocumentVolumeMerkleRoot(linkDBVolume1);
			System.out.println(DeSignCore.bytesToHexString(res));
			assertTrue(DeSignCore.bytesToHexString(res).equals(DeSignCore.bytesToHexString(localStorage.getDocumentVolumeMerkleRoot(linkLocalVolume1))));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
