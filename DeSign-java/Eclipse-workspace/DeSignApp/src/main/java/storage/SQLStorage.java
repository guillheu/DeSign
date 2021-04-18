package storage;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.BytesUtils;
import util.MerkleTree;

import java.sql.Statement;

public class SQLStorage extends DocumentVolumeStorage {

	private Connection SQLConnection;
	private String DBName, tableName, indexHashColumnName, dataColumnName;

	public SQLStorage(MessageDigest hashAlgo, String connectionLink, String DBName, String tableName, String volumeIdColumnName, String dataColumnName) {
		
		try {
            
            SQLConnection = DriverManager.getConnection(connectionLink);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("\n/!\\Make sure to load the SQL driver before calling this/!\\");
        }
		this.DBName = DBName;
		this.tableName = tableName;
		this.indexHashColumnName = volumeIdColumnName;
		this.dataColumnName = dataColumnName;
	}

	@Override
	public byte[] getIndexedDocumentVolumeMerkleRoot(String index, MessageDigest hashAlgo) throws Exception {
		String indexHash = BytesUtils.bytesToHexString(hashAlgo.digest(index.getBytes()));
		return getIndexedVolumeMerkleTree(indexHash, hashAlgo).getRoot().sig;
	}

	@Override
	public List<byte[]> getMerklePath(byte[] document, MessageDigest hashAlgo) throws Exception {
		String indexHash = getDocumentIndex(document);
		List<String> path = getIndexedVolumeMerkleTree(indexHash, hashAlgo).getMerklePath(BytesUtils.bytesToHexString(hashAlgo.digest(document)));
		List<byte[]> bytePath = new ArrayList<byte[]>();
		for(String step : path) {
			bytePath.add(BytesUtils.hexStringToByteArray(step));
		}
		return bytePath;
	}

	@Override
	public byte[] getIndexHash(byte[] document) {
		return BytesUtils.hexStringToByteArray(getDocumentIndex(document));
	}


	
	private MerkleTree getIndexedVolumeMerkleTree(String indexHash, MessageDigest hashAlgo) {
		Statement stmt = null;
		ResultSet rs = null;
		MerkleTree r = null;
		
		String query = "SELECT " + dataColumnName + " FROM " + DBName + "." + tableName + " WHERE "+ indexHashColumnName + " = 0x" + indexHash + ";";
		
		try {
			stmt = SQLConnection.createStatement();
			rs = stmt.executeQuery(query);
			List<String> res = new ArrayList<String>();

			System.err.println("running the following query : \n" + query);
			while(rs.next()) {
				res.add(BytesUtils.bytesToHexString(hashAlgo.digest(rs.getBytes(dataColumnName))));
			}
			r = new MerkleTree(res, hashAlgo);
		} catch (SQLException ex) {
		    // handle any errors
			System.err.println("Attempted query : " + query);
		    System.err.println("SQLException: " + ex.getMessage());
		    System.err.println("SQLState: " + ex.getSQLState());
		    System.err.println("VendorError: " + ex.getErrorCode());
		    ex.printStackTrace();
		}
		finally {
		    if (rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException sqlEx) { } // ignore
		        rs = null;
		    }
		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException sqlEx) { } // ignore
		        stmt = null;
		    }
		}
		return r;
	}
	
	private String getDocumentIndex(byte[] document) {
		Statement stmt = null;
		ResultSet rs = null;
		String indexHash = null;
		
		String query = "SELECT " + indexHashColumnName + " FROM " + DBName + "." + tableName + " WHERE "+ dataColumnName + " = 0x" + BytesUtils.bytesToHexString(document) + ";";
		
		try {
			stmt = SQLConnection.createStatement();
			rs = stmt.executeQuery(query);

			System.err.println("running the following query : \n" + query);
			while(rs.next()) {
				indexHash = BytesUtils.bytesToHexString(rs.getBytes(indexHashColumnName));
			}
		} catch (SQLException ex) {
		    // handle any errors
			System.err.println("Attempted query : " + query);
		    System.err.println("SQLException: " + ex.getMessage());
		    System.err.println("SQLState: " + ex.getSQLState());
		    System.err.println("VendorError: " + ex.getErrorCode());
		    ex.printStackTrace();
		}
		finally {
		    if (rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException sqlEx) { } // ignore
		        rs = null;
		    }
		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException sqlEx) { } // ignore
		        stmt = null;
		    }
		}
		return indexHash;
	}

}
