// Why ABL Example
// Authors: Bill Wood, Alan Estrada
// File Name: ComplexUpdate/java_example.java
// Version 1.02

import java.sql.*;

public class example {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/classicmodels";

    // Database credentials
    static final String USER = "root";
    static final String PASS = "password";
   
    public static void main(String[] args) {
	Connection conn = null;
	PreparedStatement preparedStmt = null;
	Statement stmt;
	ResultSet rs = null; 
	ResultSet rs2 = null; 

	String sql = null;
	java.sql.Date sqlDate = null;
	java.sql.Date sqlDate2 = null;
	
	int salesRep = 1702;
	int custNum = 999;
	int referringCustNum = 450;
	int orderNum = 20400; 
	String productCode = "S18_2325"; 
	int productQuantity = 5;
	int orderline = 20111;

	try {
	    // Register JDBC driver
	    Class.forName(JDBC_DRIVER);

	    // Open a connection
	    conn = DriverManager.getConnection(DB_URL,USER,PASS);
	    conn.setAutoCommit(false);

	    stmt = conn.createStatement();

	    // Find if customer exists
	    sql = "SELECT * FROM customers where customerNumber = " + custNum;
	    rs = stmt.executeQuery(sql);

	    // If customer doesn't exist, add the customer
	    if (!rs.isBeforeFirst() ) {  
		sql = "INSERT INTO customers " +
		    "VALUES (?, 'Warehouse of Miriam', 'Dune', 'Sue', '5555555555', 'Fake Lane'," +
		    "null, 'Exctinct City', 'MA', '55555', 'USA', ?, 8280, 1000)";
		preparedStmt = conn.prepareStatement(sql);
		preparedStmt.setInt(1, custNum);
		preparedStmt.setInt(2, salesRep);
		preparedStmt.executeUpdate();
	    }

	    // Find if referring customer exists
	    sql = "SELECT * FROM customers where customerNumber = " + referringCustNum;
	    rs = stmt.executeQuery(sql);

	    // If customer does exist, give a $50 credit
	    if (rs.isBeforeFirst()) {  
		sql = "UPDATE customers " +
		    "SET balance = balance - 50 WHERE customerNumber = ?";
		preparedStmt = conn.prepareStatement(sql);
		preparedStmt.setInt(1, referringCustNum);
		preparedStmt.executeUpdate();
	    }

	    // Create orders
	    sql = "INSERT INTO orders " +
		"VALUES (?, '2005-02-02', '2005-03-02', '2005-02-010', 'In Process', 'New customer!'," +
		"?)";
	    preparedStmt = conn.prepareStatement(sql);
	    preparedStmt.setInt(1, orderNum);
	    preparedStmt.setInt(2, custNum);
	    preparedStmt.executeUpdate();

	    // Create orderlines
	    sql = "INSERT INTO orderdetails " +
		"VALUES (?, ?, 5, 0, 20400)";
	    preparedStmt = conn.prepareStatement(sql);
	    preparedStmt.setInt(1, orderNum);
	    preparedStmt.setString(2, productCode);
	    preparedStmt.executeUpdate();

	    // Find all the orders of the customer
	    sql = "SELECT * FROM orders where customerNumber = " + custNum;
	    rs = stmt.executeQuery(sql); 

	    // If the order of the customer is within a month of another order,
	    // merge the two orders by combining their shipping dates.
	    while (rs.next()) {
		sqlDate = rs.getDate(2);
		sql = "SELECT * FROM orders where customerNumber = ? AND " + 
		    "ABS(datediff(orders.orderDate, ?)) <= 30";
		preparedStmt = conn.prepareStatement(sql);
		preparedStmt.setInt(1, custNum);
		preparedStmt.setDate(2, sqlDate);

		rs2 = preparedStmt.executeQuery();

		if (rs2.next()) {  
		    sqlDate2 = rs2.getDate(2);

		    sql = "UPDATE orders " +
			"SET orderDate = ? WHERE orderNumber = ?";	
		    preparedStmt = conn.prepareStatement(sql);

		    if (sqlDate2.before(sqlDate)) {
			preparedStmt.setDate(1, sqlDate);
			preparedStmt.setInt(2, rs2.getInt(1));
		    } else {
			preparedStmt.setDate(1, sqlDate2);
			preparedStmt.setInt(2, rs.getInt(1));
		    }
		  
		    preparedStmt.executeUpdate();
		}
	    }	    

	    conn.commit();
	    System.out.println("Executed update");
	} 
	// Handle exceptions
	catch(Exception e){
	    System.out.println(e.getMessage());
	    try {

		if (conn != null) {
		    conn.rollback();
		}
	    } catch (SQLException excep) {
		System.out.println("Rollback failed.");
		excep.printStackTrace();
	    } 	    
	} 
	// Close resources
	finally {
	    try {
		//		preparedStmt.close();
		if (conn != null) {		 
		    conn.close();
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	System.out.println("Goodbye!");
    }
}
