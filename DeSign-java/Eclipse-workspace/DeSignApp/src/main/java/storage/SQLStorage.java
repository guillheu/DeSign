package storage;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;
import util.BytesUtils;
import util.MerkleTree;
import util.MerkleTree.Node;

import java.sql.Statement;

public class SQLStorage extends DocumentVolumeStorage {

	private Connection SQLConnection;
	private String DBName, tableName, indexHashColumnName, dataColumnName;
	private String idColumnName;

	public SQLStorage(MessageDigest hashAlgo, String connectionLink, String username, String password, String DBName, String tableName, String volumeIdColumnName, String dataColumnName, String sQLIdColumnName) {
		
		try {
            
            SQLConnection = DriverManager.getConnection(connectionLink, username, password);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("\n/!\\Make sure to load the SQL driver before calling this/!\\");
        }
		this.DBName = DBName;
		this.tableName = tableName;
		this.indexHashColumnName = volumeIdColumnName;
		this.dataColumnName = dataColumnName;
		this.idColumnName = sQLIdColumnName;
	}

	@Override
	public byte[] getIndexedDocumentVolumeMerkleRoot(String index, MessageDigest hashAlgo) throws Exception {
		String indexHash = BytesUtils.bytesToHexString(hashAlgo.digest(index.getBytes()));
		return getIndexedVolumeMerkleTree(indexHash, hashAlgo).getRoot().sig;
	}

	@Override
	public List<Pair<String, String>> getMerklePath(byte[] document, MessageDigest hashAlgo) throws Exception {
		String indexHash = getDocumentIndex(document);
		return getIndexedVolumeMerkleTree(indexHash, hashAlgo).getMerklePath(BytesUtils.bytesToHexString(hashAlgo.digest(document)));
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

			//System.err.println("running the following query : \n" + query);
			while(rs.next()) {
				res.add(BytesUtils.bytesToHexString(hashAlgo.digest(rs.getBytes(dataColumnName))));
			}
			
			System.out.println("NUMBER OF SIGS = " + res.size());
			if(res.size() == 1) {
				Node root = new Node();
				root.sig = BytesUtils.hexStringToByteArray(res.get(0));
				root.type = MerkleTree.ROOT_SIG_TYPE;
				r = new MerkleTree(root, 1, 1, res);
			}
			else {
				r = new MerkleTree(res, hashAlgo);
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

			//System.err.println("running the following query : \n" + query);
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

	@Override
	public void importDocument(byte[] document, String index, MessageDigest hashAlgo) {
		Statement stmt = null;
		
		String query = "INSERT INTO " + DBName + "." + tableName + " (" + indexHashColumnName + "," + dataColumnName + ") VALUES (" + "0x" + BytesUtils.bytesToHexString(hashAlgo.digest(index.getBytes())) + ", 0x" + BytesUtils.bytesToHexString(document) + ");";
		
		try {
			stmt = SQLConnection.createStatement();
			stmt.executeUpdate(query);

			//System.err.println("running the following query : \n" + query);
		} catch (SQLException ex) {
		    // handle any errors
			System.err.println("Attempted query : " + query);
		    System.err.println("SQLException: " + ex.getMessage());
		    System.err.println("SQLState: " + ex.getSQLState());
		    System.err.println("VendorError: " + ex.getErrorCode());
		    ex.printStackTrace();
		}
		finally {
		    
		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException sqlEx) { } // ignore
		        stmt = null;
		    }
		}
	}

	@Override
	public byte[] getDocumentFromID(int documentID) {
		Statement stmt = null;
		ResultSet rs = null;
		byte[] documentBytes = null;
		
		String query = "SELECT " + dataColumnName + " FROM " + DBName + "." + tableName + " WHERE "+ idColumnName  + " = " + documentID + ";";
		
		try {
			stmt = SQLConnection.createStatement();
			rs = stmt.executeQuery(query);

			//System.err.println("running the following query : \n" + query);
			while(rs.next()) {
				documentBytes = rs.getBytes(dataColumnName);
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
		return documentBytes;
	}

}
