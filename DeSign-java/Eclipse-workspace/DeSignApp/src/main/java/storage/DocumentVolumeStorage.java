package storage;

import java.security.MessageDigest;

public abstract class DocumentVolumeStorage {
	public abstract byte[] getIndexedDocumentVolumeMerkleRoot(String index, MessageDigest hashAlgo) throws Exception;
}
