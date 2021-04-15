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

	private static final String dataColumn = "data";
	
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
		Statement stmt = null;
		ResultSet rs = null;
		byte[] r = null;
		try {
			stmt = SQLConnection.createStatement();
			rs = stmt.executeQuery(link);
			List<String> res = new ArrayList<String>();
			while(rs.next()) {
				res.add(DeSignCore.bytesToHexString(hashAlgo.digest(rs.getBytes(dataColumn))));
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

	@Override
	public void createDocumentVolume(String link, List<byte[]> documentBinaries) throws Exception {
		
	}

}
