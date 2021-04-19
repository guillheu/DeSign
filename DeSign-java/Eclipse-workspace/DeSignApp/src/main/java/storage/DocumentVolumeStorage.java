package storage;

import java.security.MessageDigest;
import java.util.List;

import kotlin.Pair;

public abstract class DocumentVolumeStorage {
	public abstract byte[] getIndexedDocumentVolumeMerkleRoot(String index, MessageDigest hashAlgo) throws Exception;


	public abstract byte[] getIndexHash(byte[] document);

	public abstract List<Pair<String,String>> getMerklePath(byte[] document, MessageDigest hashAlgo) throws Exception;
	
	public abstract void importDocument(byte[] document, String index, MessageDigest hashAlgo);
}
