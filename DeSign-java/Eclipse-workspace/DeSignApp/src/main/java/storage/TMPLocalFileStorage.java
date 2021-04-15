package storage;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import core.DeSignCore;
import util.MerkleTree;

public class TMPLocalFileStorage extends DocumentVolumeStorage {



	public TMPLocalFileStorage(MessageDigest hashAlgo) {
		super(hashAlgo);
	}



	public static final int maxVolumeSize = 4;
	
	
	
	@Override
	public byte[] getDocumentVolumeMerkleRoot(String link) throws Exception {
		ArrayList<byte[]> docSigsBytes = new ArrayList<byte[]>();
		for(int i = 1; i < maxVolumeSize+1; i++) {
			docSigsBytes.add(hashAlgo.digest(FileUtils.readFileToByteArray(new File(link + "document" + i))));
		}
		ArrayList<String> docSigsStrings = new ArrayList<String>();
		for(byte[] hash : docSigsBytes) {
			docSigsStrings.add(DeSignCore.bytesToHexString(hash));
		}
		MerkleTree tree = new MerkleTree(docSigsStrings);
		return tree.getRoot().sig;
	}



	@Override
	public void createDocumentVolume(String link, List<byte[]> documentBinaries) throws Exception {
		throw new Exception("Not implemented");
	}
	
	
	
	
}
