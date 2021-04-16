package storage;

import java.security.MessageDigest;

public abstract class DocumentVolumeStorage {
	public abstract byte[] getDocumentVolumeMerkleRoot(String link, MessageDigest hashAlgo) throws Exception;
	public abstract String getLinkFromVolumeID(String volumeID);
}
