package storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import core.DeSignCore;
import util.MerkleTree;

public class TMPLocalFileStorage implements DocumentVolumeStorage {
	public static final int maxVolumeSize = 4;
	@Override
	public byte[] getDocumentVolumeMerkleRoot(String link) throws IOException {
		ArrayList<byte[]> docSigsBytes = new ArrayList<byte[]>();
		for(int i = 1; i < maxVolumeSize+1; i++) {
			docSigsBytes.add(DeSignCore.sha256.digest(FileUtils.readFileToByteArray(new File(link + "document" + i))));
		}
		ArrayList<String> docSigsStrings = new ArrayList<String>();
		for(byte[] hash : docSigsBytes) {
			docSigsStrings.add(DeSignCore.bytesToHexString(hash));
		}
		MerkleTree tree = new MerkleTree(docSigsStrings);
		return tree.getRoot().sig;
	}
	
	
	
	
}
