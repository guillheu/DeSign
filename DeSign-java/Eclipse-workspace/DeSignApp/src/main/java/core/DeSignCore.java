package core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import kotlin.Pair;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Convert;

import contractWrappers.DeSign;
import storage.DocumentVolumeStorage;
import util.BytesUtils;

public class DeSignCore {
	private Web3j web3;
	private DeSign contract;
	private DocumentVolumeStorage storage;
	private MessageDigest hashAlgo;
	private ContractGasProvider gasProvider;

	private final byte[] defaultAdminRole = BytesUtils.hexStringToByteArray("0000000000000000000000000000000000000000000000000000000000000000");;
	private final byte[] signatoryRole = BytesUtils.hexStringToByteArray("9838a05512653d899e198165cc2a8305c24ac29892037f9cab63bdb153121845");
	




	
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
	
	

	public Tuple3<byte[], BigInteger, String> getIndexInfo(String index) throws Exception {
		byte[] indexHash = hashAlgo.digest(index.getBytes());
		return contract.getIndexData(indexHash).send();
	}
	
	public TransactionReceipt signMerkleRoot(byte[] indexHash, byte[] merkleRoot, int daysBeforeExpiration) throws Exception {
		return contract.signMerkleRoot(indexHash, merkleRoot, BigInteger.valueOf(daysBeforeExpiration)).send();
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
	
	public TransactionReceipt sign(String index, int lifetime) throws Exception {
		return signMerkleRoot(hashAlgo.digest(index.getBytes()), storage.getIndexedDocumentVolumeMerkleRoot(index, hashAlgo), lifetime);
	}
	
	public Boolean checkSignature(String index) throws Exception {
		Tuple3<byte[], BigInteger, String> r = getIndexInfo(index);
		return (BytesUtils.bytesToHexString(r.component1()).equals(BytesUtils.bytesToHexString(storage.getIndexedDocumentVolumeMerkleRoot(index, hashAlgo))));
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

	public SignatureProof getSignatureProof(byte[] document, String nodeURL) {
		SignatureProof r = new SignatureProof();
		
		try {
			for(Pair<String,String> step : storage.getMerklePath(document, hashAlgo)) {
				Pair<String,String> entry = new Pair<String,String>(step.component1(),"0x"+step.component2());
				r.merklePath.add(entry);
			}
			
		} catch (NullPointerException e) {
			System.err.println("Merkle tree only contains a single document, moving on...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		r.indexHash = "0x"+BytesUtils.bytesToHexString(storage.getIndexHash(document));
		r.contractAddr = contract.getContractAddress();
		r.nodeURL = nodeURL;
		r.hashAlgo = hashAlgo.getAlgorithm();
		return r;
	}
	
	public static class SignatureProof{
		public List<Pair<String,String>> merklePath = new ArrayList<Pair<String,String>>();
		public String indexHash;
		public String contractAddr;
		public String nodeURL;
		public String hashAlgo;
	}

	public void indexDocumentIntoStorage(byte[] document, String index) {
		storage.importDocument(document, index, hashAlgo);
	}

	public byte[] getDocumentFromID(int documentID) {
		
		return storage.getDocumentFromID(documentID);
	}

	
	

	public boolean isSignatory(String address) throws Exception {
		return contract.hasRole(signatoryRole, address).send();
	}

	public boolean isDefaultAdmin(String address) throws Exception {
		return contract.hasRole(defaultAdminRole, address).send();
	}

	public TransactionReceipt makeSignatory(String address) throws Exception {
		return contract.grantRole(signatoryRole, address).send();
	}

	public TransactionReceipt revokeSignatory(String address) throws Exception {
		return contract.revokeRole(signatoryRole, address).send();
	}
}
