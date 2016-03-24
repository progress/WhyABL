/* Why ABL Example
   Authors: Bill Wood, Alan Estrada
   File Name: TempTable/result_set.java
   Version: 0.1

   This file shows the usage of result sets in Java.7

*/

import java.sql.*;
import java.util.*;

public class result_set {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/classicmodels";

    // Database credentials
    static final String USER = "root";
    static final String PASS = "password";
   
    public static void main(String[] args) {
	Connection conn = null;
	PreparedStatement preparedStmt = null;
	Statement stmt = null;
	ResultSet rs = null;

	// An array list of an array list of strings is how we're going to
	// have to represent a table.
	ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>(); 

	// preparedStmt = conn.prepareStatement(update);	    	    
	// preparedStmt.executeUpdate();

	try {
	    // Register JDBC driver
	    Class.forName("com.mysql.jdbc.Driver");

	    // Open a connection
	    conn = DriverManager.getConnection(DB_URL,USER,PASS);

	    stmt = conn.createStatement();

	    // Execute a query
	    String sql = "SELECT * FROM products";
	    
	    rs = stmt.executeQuery(sql);

	    // Navigate to a row that fulfills a certain condition
	    while (rs.next()) {
		// Grab the product code. This requires you to know
		// the schema beforehand.
		String s = rs.getString("productCode");
		
		if (s.equals("S10_1949")) {
		    System.out.println("Product Code: " + rs.getString("productCode") + "\n" +
				       "Product Name: " + rs.getString("productName") + "\n");
		}
	    }
	   
 	    // Rewind result set
	    rs.beforeFirst(); 

	    // Sort from highest MSRP to lowest and filter out 
	    // items where we have stock less than 5000 

	    // Iterate through the result set, filtering out 
	    // the unwanted ones. 
	    while (rs.next()) {
		if (rs.getDouble("quantityInStock") > 5000) {
		    // This ArrayList represents a row
		    ArrayList<String> row = new ArrayList<String>();

		    // Add each column to our ArrayList
		    row.add(rs.getString("productCode"));
		    row.add(rs.getString("productName"));
		    row.add(rs.getString("productLine"));
		    row.add(rs.getString("productScale"));
		    row.add(rs.getString("productVendor"));
		    row.add(rs.getString("productDescription"));
		    row.add(Double.toString(rs.getDouble("MSRP")));

		    // Add our makeshift row to our makeshift table
		    table.add(row);
		}
	    }

	    // Sort our table by item MSRP
	    Collections.sort(table, new MSRPCompare()) ;
		
	    // Print out our sorted result set.
	    for (ArrayList<String> row : table) {	       
		System.out.println("Product Name: " + row.get(1) + "\n" +
				   "MSRP: " + row.get(6) + "\n");
	    }

	    // Rewind result set and clear our table implementation
	    rs.beforeFirst(); 
	    table.clear();
	    
	} 
	// Handle exceptions
	catch(Exception e){
	    System.out.println(e.getMessage());
	} 
	// Close resources
	finally {
	    try {
		stmt.close();
		// preparedStmt.close();
		conn.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	System.out.println("Goodbye!");
    }
}

// This class implements the comparison function that Collections
// will use to sort the rows based on MSRP 
class MSRPCompare implements Comparator<ArrayList<String>> {
    
    @Override
    public int compare(ArrayList<String> al1, ArrayList<String> al2) {
	// Compare the MSRP of the row and return the higher one
	boolean result = Double.parseDouble(al1.get(6)) < Double.parseDouble(al2.get(6));
	return result ? 1 : -1 ; 
    }
}