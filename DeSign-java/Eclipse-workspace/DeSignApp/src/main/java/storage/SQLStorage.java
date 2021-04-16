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

	private static final String delimiterCharacterInLink = ":";
	private Connection SQLConnection;

	public SQLStorage(MessageDigest hashAlgo, String connectionLink) {
		super(hashAlgo);
		try {
            
            SQLConnection = DriverManager.getConnection(connectionLink);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("\n/!\\Make sure to load the SQL driver before calling this/!\\");
        }
	}

	@Override
	public byte[] getDocumentVolumeMerkleRoot(String link) throws Exception {
		String[] splits = link.split(delimiterCharacterInLink);
		if(splits.length != 3) {
			throw new Exception("Invalid link, should contain a single \"" + delimiterCharacterInLink +"\" ; was \""+ link + "\"");
		}
		String DB = splits[0];
		String volumeID = splits[1];
		String column = splits[2];
		Statement stmt = null;
		ResultSet rs = null;
		byte[] r = null;
		
		String query = "SELECT " + column + " FROM " + DB + " WHERE volume_id = " + volumeID + ";";
		
		try {
			stmt = SQLConnection.createStatement();
			rs = stmt.executeQuery(query);
			List<String> res = new ArrayList<String>();
			while(rs.next()) {
				res.add(DeSignCore.bytesToHexString(hashAlgo.digest(rs.getBytes(column))));
			}
			r = new MerkleTree(res).getRoot().sig;
		} catch (SQLException ex) {
		    // handle any errors
		    System.err.println("SQLException: " + ex.getMessage());
		    System.err.println("SQLState: " + ex.getSQLState());
		    System.err.println("VendorError: " + ex.getErrorCode());
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
