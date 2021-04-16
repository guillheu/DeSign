package core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.ExecutionException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Convert;

import contractWrappers.DeSign;
import storage.DocumentVolumeStorage;

public class DeSignCore {
	private Web3j web3;
	private DeSign contract;
	private DocumentVolumeStorage storage;
	private MessageDigest hashAlgo;
	private ContractGasProvider gasProvider;
	




	
	/*
	 * 
	 * Constructors
	 * 
	 * */
	
	//To use with a pre-deployed contract
	public DeSignCore(String nodeURL, String contractAddr, Credentials credentials, ContractGasProvider gasProvider, DocumentVolumeStorage storage, MessageDigest hashAlgo) {
		this.hashAlgo = hashAlgo;
		this.storage = storage;
		this.gasProvider = gasProvider;
		web3  = Web3j.build(new HttpService(nodeURL));
		contract  = DeSign.load(contractAddr, web3, credentials, gasProvider);
	}
	
	//Will deploy a contract
	public DeSignCore(String nodeURL, Credentials creds, ContractGasProvider gasProvider, DocumentVolumeStorage storage, MessageDigest hashAlgo) throws Exception {
		this.hashAlgo = hashAlgo;
		this.storage = storage;
		this.gasProvider = gasProvider;
		web3  = Web3j.build(new HttpService(nodeURL));
		contract = DeSign.deploy(web3, creds, gasProvider).send();
		System.err.println("new contract deployed at address " + contract.getContractAddress());
	}
	
	/*
	 * 
	 * Blockchain interaction methods
	 * 
	 */
	
	

	public Tuple2<byte[], BigInteger> getIndexInfo(String index) throws Exception {
		byte[] indexHash = hashAlgo.digest(index.getBytes());
		return contract.getIndexData(indexHash).send();
	}
	
	public void signMerkleRoot(byte[] indexHash, byte[] merkleRoot, int daysBeforeExpiration) throws Exception {
		contract.signMerkleRoot(indexHash, merkleRoot, BigInteger.valueOf(daysBeforeExpiration)).send();
	}
	
	public BigDecimal getAddressBalance(String address) throws InterruptedException, ExecutionException {
		return Convert.fromWei( new BigDecimal(web3
		  .ethGetBalance(address, DefaultBlockParameterName.LATEST)
		  .sendAsync()
		  .get().getBalance()), Convert.Unit.ETHER);
	}
	
	/*
	 * 
	 * Full flow functions
	 * 
	 */
	
	public void sign(String index, int lifetime) throws Exception {
		signMerkleRoot(hashAlgo.digest(index.getBytes()), storage.getIndexedDocumentVolumeMerkleRoot(index, hashAlgo), lifetime);
	}
	
	public Boolean checkSignature(String index) throws Exception {
		Tuple2<byte[], BigInteger> r = getIndexInfo(index);
		System.out.println("found signature : " + bytesToHexString(r.component1()));
		return (bytesToHexString(r.component1()).equals(bytesToHexString(storage.getIndexedDocumentVolumeMerkleRoot(index, hashAlgo))));
	}
	
	
	/*
	 * 
	 * Utility methods
	 * 
	 */
	
	public static String bytesToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes){
            sb.append(String.format("%02x", b&0xff));
        }
        return sb.toString();
    }




	/*
	 * 
	 * Proxy getters & setters
	 * 
	 */
	
	public String getContractAddress() {
		return contract.getContractAddress();
	}

	public byte[] getDocumentVolumeMerkleRoot(String index, MessageDigest hashAlgo) throws Exception {
		return storage.getIndexedDocumentVolumeMerkleRoot(index, hashAlgo);
	}

	public void setCredentials(Credentials newCreds) {
		String currentAddress = contract.getContractAddress();
		contract = DeSign.load(currentAddress, web3, newCreds, gasProvider);
	}
}
