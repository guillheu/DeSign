package storage;

import java.security.MessageDigest;
import java.util.List;

public abstract class DocumentVolumeStorage {
	

	public DocumentVolumeStorage(MessageDigest hashAlgo) {
		this.hashAlgo = hashAlgo;
	}
	
	
	protected MessageDigest hashAlgo;
	
	public abstract byte[] getDocumentVolumeMerkleRoot(String link) throws Exception;
	public MessageDigest getHashAlgo() {
		return hashAlgo;
	}
}
