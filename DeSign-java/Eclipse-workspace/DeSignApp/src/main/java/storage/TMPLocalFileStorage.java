package storage;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import kotlin.Pair;
import util.BytesUtils;
import util.MerkleTree;

public class TMPLocalFileStorage extends DocumentVolumeStorage {




	private String rootStoragePath;

	public TMPLocalFileStorage(String rootStoragePath) {
		super();
		this.rootStoragePath = rootStoragePath;
	}



	public static final int maxVolumeSize = 4;
	
	
	
	@Override
	public byte[] getIndexedDocumentVolumeMerkleRoot(String index, MessageDigest hashAlgo) throws Exception {
		String indexHash = BytesUtils.bytesToHexString(hashAlgo.digest(index.getBytes()));
		ArrayList<byte[]> docSigsBytes = new ArrayList<byte[]>();
		for(int i = 1; i < maxVolumeSize+1; i++) {
			docSigsBytes.add(hashAlgo.digest(FileUtils.readFileToByteArray(new File(rootStoragePath + indexHash + "/document" + i))));
		}
		ArrayList<String> docSigsStrings = new ArrayList<String>();
		for(byte[] hash : docSigsBytes) {
			docSigsStrings.add(BytesUtils.bytesToHexString(hash));
		}
		MerkleTree tree = new MerkleTree(docSigsStrings, hashAlgo);
		return tree.getRoot().sig;
	}






	@Override
	public byte[] getIndexHash(byte[] document) {
		// TODO Auto-generated method stub
		return null;
	}






	@Override
	public List<Pair<String, String>> getMerklePath(byte[] document, MessageDigest hashAlgo) {
		// TODO Auto-generated method stub
		return null;
	}






	@Override
	public void importDocument(byte[] document, String index, MessageDigest hashAlgo) {
		// TODO Auto-generated method stub
		
	}






	@Override
	public byte[] getDocumentFromID(int documentID) {
		// TODO Auto-generated method stub
		return null;
	}




	
	
	
}
