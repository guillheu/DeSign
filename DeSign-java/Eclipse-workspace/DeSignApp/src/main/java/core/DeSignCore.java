package core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.ExecutionException;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import contractWrappers.DeSign;
import storage.DocumentVolumeStorage;

public class DeSignCore {
	public Web3j web3;
	public DeSign contract;
	private DocumentVolumeStorage storage;
	
	private MessageDigest sha256;
	
	/*
	 * 
	 * setup methods
	 * 
	 * */
	public DeSignCore(String nodeURL, String contractAddr, Credentials credentials, DocumentVolumeStorage storage) {
		super();
		sha256 = storage.getHashAlgo();
		this.storage = storage;
		web3  = Web3j.build(new HttpService(nodeURL));
		contract  = DeSign.load(contractAddr, web3, credentials, new DefaultGasProvider());	//TODO : change the gas provider when updating for production
	}

	
	/*
	 * 
	 * Blockchain interaction methods
	 * 
	 */
	
	public Tuple3<byte[], byte[], BigInteger> getIndexInfo(String index) throws Exception {
		return contract.getIndexData(sha256.digest(index.getBytes())).send();
		
	}
	
	public void signMerkleRoot(byte[] indexHash, byte[] merkleRoot, byte[] link, int daysBeforeExpiration) throws Exception {
		contract.signMerkleRoot(indexHash, merkleRoot, link, BigInteger.valueOf(daysBeforeExpiration)).send();
	}
	
	public BigDecimal getAddressBalance(String address) throws InterruptedException, ExecutionException {
		return Convert.fromWei( new BigDecimal(web3
		  .ethGetBalance(address, DefaultBlockParameterName.LATEST)
		  .sendAsync()
		  .get().getBalance()), Convert.Unit.ETHER);
	}
	
	/*
	 * 
	 * Coordination functions
	 * 
	 */
	
	public void sign(String index, String documentVolumeLink, int lifetime) throws Exception {
		signMerkleRoot(sha256.digest(index.getBytes()), storage.getDocumentVolumeMerkleRoot(documentVolumeLink), documentVolumeLink.getBytes(), lifetime);
	}
	
	public Boolean checkSignature(String index) throws Exception {
		Tuple3<byte[], byte[], BigInteger> r = getIndexInfo(index);
		return (bytesToHexString(r.component1()).equals(bytesToHexString(storage.getDocumentVolumeMerkleRoot(new String(r.component2(), StandardCharsets.UTF_8)))));
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


	 

}
