// Why ABL Example
// Authors: Bill Wood, Alan Estrada
// File Name: BasicQuery/java_example.java
// Version 11.6.1
// 
// This is the Java equivalent of this slice of ABL code:
//
// FOR EACH Customer WHERE Customer.SalesRep = repname:
//     IF Balance > CreditLimit THEN Balance = Balance + 5.
// END.

import java.sql.*;

public class java_example {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/classicmodels";

    // Database credentials
    static final String USER = "root";
    static final String PASS = "";
   
    public static void main(String[] args) {
	Connection conn = null;
	PreparedStatement preparedStmt = null;
	int i = 1702;

	try {
	    // Register JDBC driver
	    Class.forName("com.mysql.jdbc.Driver");

	    // Open a connection
	    conn = DriverManager.getConnection(DB_URL,USER,PASS);
	    conn.setAutoCommit(false);

	    // Execute a query
	    String update = 
		"UPDATE customers " +
		"SET BALANCE = BALANCE + 0.05 " +
		"WHERE SALESREPEMPLOYEENUMBER = ? AND BALANCE > CREDITLIMIT";
	    
	    preparedStmt = conn.prepareStatement(update);
	    	    
	    // Replace the format specifier with a value
	    preparedStmt.setInt(1, i);
	
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
		conn.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	System.out.println("Goodbye!");
    }
}