/* Why ABL Example
   Authors: Bill Wood, Alan Estrada
   File Name: OperateOnResults/example.java
   Version 1.02
*/

import java.sql.*;
import java.io.PrintWriter;

public class example {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/classicmodels";

    // Database credentials
    static final String USER = "root";
    static final String PASS = "";
   
    public static void main(String[] args) {
	Connection conn = null;
	Statement stmt = null;

	try {
	    // Register JDBC driver
	    Class.forName(JDBC_DRIVER);

	    // Open a connection and create a SQL statement
	    conn = DriverManager.getConnection(DB_URL,USER,PASS);
	    stmt = conn.createStatement();

	    // This section is analagous to the ABL code: 
	    // FOR EACH Customer WHERE Customer.SalesRep = "DOS":
	    //    DISPLAY CustNum Name Balance.
	    // END.

	    // Execute a query
	    String sql =
		"SELECT * FROM customers WHERE salesRepEmployeeNumber = 1702";	   

	    ResultSet rs = stmt.executeQuery(sql);

	    int custNum;
	    String name;
	    double balance;

	    while (rs.next()) {
		// Retrive by column name
		custNum = rs.getInt("customerNumber");
		name = rs.getString("customerName");
		balance = rs.getDouble("balance");

		System.out.println("Customer Number: " + custNum);
		System.out.println("Customer Name: " + name);
		System.out.println("Customer Balance: " + balance);
	    }

	    // This section is analagous to the ABL code:
	    // OUTPUT TO "report.txt".
	    // FOR EACH Customer WHERE Customer.SalesRep = "DOS":
	    //    PUT "ID: "  CustNum " Name: " Name " Balance: " Balance SKIP.
	    // END.
	    // OUTPUT CLOSE.	    

	    // Reset the result set so we can re-use the data
	    rs.beforeFirst();

	    PrintWriter writer = new PrintWriter("report.txt", "UTF-8");

	    while (rs.next()) {
		// Retrive by column name
		custNum = rs.getInt("customerNumber");
		name = rs.getString("customerName");
		balance = rs.getDouble("balance");

		writer.println("Customer Number: " + custNum);
		writer.println("Customer Name: " + name);
		writer.println("Customer Balance: " + balance);
	    }

	    writer.close();

	} 
	// Handle exceptions
	catch(Exception e){
	    System.out.println(e.getMessage());
	} 
	// Close resources
	finally {
	    try {
		stmt.close();
	    } catch (SQLException e) {
		System.out.println(e.getMessage());
	    }
	    try {
		conn.close();
	    } catch (SQLException e) {
		System.out.println(e.getMessage());
	    }
	}
	System.out.println("Goodbye!");
    }
}
