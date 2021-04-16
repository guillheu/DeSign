package storage;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import core.DeSignCore;
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
		String indexHash = DeSignCore.bytesToHexString(hashAlgo.digest(index.getBytes()));
		Statement stmt = null;
		ResultSet rs = null;
		byte[] r = null;
		
		String query = "SELECT " + dataColumnName + " FROM " + DBName + "." + tableName + " WHERE "+ indexHashColumnName + " = 0x" + indexHash + ";";
		
		try {
			stmt = SQLConnection.createStatement();
			rs = stmt.executeQuery(query);
			List<String> res = new ArrayList<String>();

			System.err.println("running the following query : \n" + query);
			while(rs.next()) {
				res.add(DeSignCore.bytesToHexString(hashAlgo.digest(rs.getBytes(dataColumnName))));
			}
			r = new MerkleTree(res, hashAlgo).getRoot().sig;
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


	

	
	

}
