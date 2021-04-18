package storage;

import java.security.MessageDigest;
import java.util.List;

public abstract class DocumentVolumeStorage {
	public abstract byte[] getIndexedDocumentVolumeMerkleRoot(String index, MessageDigest hashAlgo) throws Exception;


	public abstract byte[] getIndexHash(byte[] document);

	public abstract List<byte[]> getMerklePath(byte[] document, MessageDigest hashAlgo) throws Exception;
}
