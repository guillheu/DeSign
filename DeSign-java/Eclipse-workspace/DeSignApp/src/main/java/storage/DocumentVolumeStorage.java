package storage;

import java.io.IOException;

public interface DocumentVolumeStorage {
	public byte[] getDocumentVolumeMerkleRoot(String link) throws IOException;
}
