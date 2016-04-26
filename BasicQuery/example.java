// Why ABL Example
// Authors: Bill Wood, Alan Estrada
// File Name: BasicQuery/example.java
// Version 1.04
// 
// This is the Java equivalent of this slice of ABL code:
//
// FOR EACH Customer WHERE SalesRep = repname AND Balance > CreditLimit:
//    Balance = Balance * creditFactor.
// END.

import java.sql.*;

public class example {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/sports2000";

    // Database credentials
    static final String USER = "root";
    static final String PASS = "";
   
    public static void main(String[] args) {
	Connection conn = null;
	PreparedStatement preparedStmt = null;

	try {
	    // Register JDBC driver
	    Class.forName(JDBC_DRIVER);

	    // Open a connection
	    conn = DriverManager.getConnection(DB_URL,USER,PASS);
	    conn.setAutoCommit(false);

	    // Execute a query
	    String sql =
		"UPDATE customers " +
		"SET BALANCE = BALANCE * ?" +
		"WHERE SALESREPEMPLOYEENUMBER = ? AND BALANCE > CREDITLIMIT";
	    
	    preparedStmt = conn.prepareStatement(sql);

	    // Replace the format specifiers with a value
	    double creditFactor = 1.05; 
	    preparedStmt.setDouble(1, creditFactor);

	    String repName = "GPE";
	    preparedStmt.setString(2, repName);
	    
	    preparedStmt.executeUpdate();

	    conn.commit();
	    System.out.println("Executed update");
	} 
	// Handle exceptions
	catch(Exception e){
	    System.out.println(e.getMessage());
	    try {
		conn.rollback();
	    } catch (SQLException excep) {
		System.out.println("Rollback failed.");
		excep.printStackTrace();
	    }     
	} 
	// Close resources
	finally {
	    try {
		preparedStmt.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	    try {
		conn.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	System.out.println("Goodbye!");
    }
}
