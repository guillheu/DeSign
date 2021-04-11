import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.DefaultGasProvider;

import contractWrappers.DeSign;
import core.DeSignCore;
import storage.DocumentVolumeStorage;
import storage.TMPLocalFileStorage;

public class DeSignCore_test {
	static String indexVolume1 = "A girl with a short skirt and a long jacket";
	static String indexVolume2 = "Somebody to love";
	static byte[] dataHash;
	static String linkVolume1 = "src/test/resources/DocumentVolume1/";
	static String linkVolume2 = "src/test/resources/DocumentVolume2/";
	static String account1 = "0xcC4c253F2210382D8EF88b38DA6a7DB8351EB7b2";
	static String account2 = "0x74DCaFDc8591f44a1516b0CB2B979223b3c4fad1";
	static String addr = "0x914938faD3B24002A3416e46F85e69CeB59Ddf17";
	static String nodeURL = "http://127.0.0.1:8545";
	static Credentials creds = Credentials.create("0xe1b53fdd877a9f3d78cf382d8571a1d357634c55c3f4e83c48f2155ccfdd3518");
	static DocumentVolumeStorage storage = new TMPLocalFileStorage();
	static DeSignCore dsc = new DeSignCore(nodeURL, addr, creds, storage);
	static int daysBeforeExpiration = 365;
	static int amountOfDocumentsTest = 4;
	static {
		try {
			dataHash = DeSignCore.sha256.digest(FileUtils.readFileToByteArray(new File("src/test/resources/Document")));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Test
	public void testIndexation() {		
		System.out.println("send document hash = " + DeSignCore.bytesToHexString(dataHash) + "\n" +
				"send link = " + linkVolume1 + "\n" +
				"send expiration time = " + daysBeforeExpiration);
		
			try {
				dsc.signMerkleRoot(DeSignCore.sha256.digest(indexVolume1.getBytes()),dataHash, linkVolume1.getBytes(), daysBeforeExpiration);
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
				assertEquals(new String(r.component2(), StandardCharsets.UTF_8), linkVolume1);
				assertEquals(r.component3().divide(BigInteger.valueOf(86400)).intValue(), daysBeforeExpiration);
			} catch (Exception e) {
				//e.printStackTrace();
			}
	}
	
	@Test
	public void testMerkleStorage() {
		

		
		try {
			System.out.println("volume1 root : " +  DeSignCore.bytesToHexString(storage.getDocumentVolumeMerkleRoot(linkVolume1)) + "\n" + 
			"volume2 root : " + DeSignCore.bytesToHexString(storage.getDocumentVolumeMerkleRoot(linkVolume2)));
			assertNotEquals(DeSignCore.bytesToHexString(storage.getDocumentVolumeMerkleRoot(linkVolume1)), DeSignCore.bytesToHexString(storage.getDocumentVolumeMerkleRoot(linkVolume2)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

			
			
			
	
		
		
	}
	
	@Test
	public void testIntegration() {
		try {
			String oldAddress = dsc.contract.getContractAddress();
			System.out.println("Previous contract address : " + oldAddress);
			dsc.contract.setContractAddress(DeSign.deploy(dsc.web3, creds, new DefaultGasProvider()).send().getContractAddress());
			System.out.println("smart contract redeployed at address " + dsc.contract.getContractAddress());
			fullCycle(indexVolume1, linkVolume1, daysBeforeExpiration);
			fullCycle(indexVolume2, linkVolume2, daysBeforeExpiration);
			fullCycle(indexVolume1, linkVolume2, daysBeforeExpiration);
			fullCycle(indexVolume2, linkVolume1, daysBeforeExpiration);
			System.out.println("Reverting contract address back to " + oldAddress);
			dsc.contract.setContractAddress(oldAddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void fullCycle(String index, String link, int lifetime) throws Exception {
		System.out.println("*****************************************************\n"+
				   "********************START OF CYCLE*******************\n"+
				   "*****************************************************\n\n"+
				   "Using : \n"+
				   "\tindex : " + index + "\n" +
				   "\tlink : " + link + "\n\n" +
				   "Fetching merkle root from storage...\n");
		

		
		
		
		String merkleRootStorage = DeSignCore.bytesToHexString(storage.getDocumentVolumeMerkleRoot(link));
		System.out.println("Found merkle root : " + merkleRootStorage);
		System.out.println("\nSigning & indexing the merkle root on the smart contract");
		
		dsc.sign(index, link, lifetime);
		
		String merkleRootSC = DeSignCore.bytesToHexString(dsc.getIndexInfo(index).component1());
		
		System.out.println("Fetched merkle root from smart contract : " + merkleRootSC);
		System.out.println("Checking signatures...");
		assertEquals(merkleRootStorage, merkleRootSC);
		
		boolean check = dsc.checkSignature(index);
		assertTrue(check);
		if(check)
			System.out.println("Success !\n");
		
		
	}
	
}
