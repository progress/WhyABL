/* Why ABL Example
   Authors: Bill Wood, Alan Estrada
   File Name: TempTable/result_set.java
   Version: 1.0

   This example aims to show the added difficulty of navigating,
   sorting, and filtering through result sets from queries. 

   A lot of the logic was deliberately not done in the query as 
   the aim is to present the usage of result sets. We only do two
   database queries in this example, one from the orderdetails
   table and one from the products table.
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
	Statement stmt1 = null;
	Statement stmt2 = null;
	ResultSet products = null;
	ResultSet orderdetails = null;
	String sql; 

	// An array list of an array list of strings is how we're going to
	// have to represent a table.
	ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>(); 

	try {
	    // Register JDBC driver
	    Class.forName("com.mysql.jdbc.Driver");

	    // Open a connection
	    conn = DriverManager.getConnection(DB_URL,USER,PASS);

	    stmt1 = conn.createStatement();
	    stmt2 = conn.createStatement();

	    // Store the tables products and orderdetails locally
	    sql = "SELECT * FROM products";
	    products = stmt1.executeQuery(sql);

	    sql = "SELECT * FROM orderdetails";
	    orderdetails = stmt2.executeQuery(sql);

	    // This section is analagous to the ABL code:
	    // FIND FIRST product where productVendor = Classic Metal Creations.
	    products.beforeFirst();

	    while (products.next()) {
		// When extracting column values, the user has to check the schema 
		// to make sure that the variables to contain these values have
		// matching types.
		String productVendor = products.getString("productVendor");
		String productName = products.getString("productName");

		if (productVendor.equals("Classic Metal Creations")) {
		    System.out.println("Product Vendor: " + productVendor + "\n" +
				       "Product Name: " + productName + "\n");
		    break;
		}
	    }
	   
	    // This section is analagous to the ABL code:
	    // FIND LAST orderdetails where productCode = 'S18_3232'.    
	    
	    // Go to the last entry
	    orderdetails.afterLast();

	    while (orderdetails.previous()) {
		String productCode = orderdetails.getString("productCode");
		int orderNumber = orderdetails.getInt("orderNumber");

		if (productCode.equals("S18_3232")) {
		    System.out.println("Order Number: " + orderNumber + "\n" +
				       "Product Code: " + productCode + "\n" );
		    break;
		}
	    }

	    // This section is analagous to the ABL code:
	    // FIND PREV orderdetails where productCode = 'S18_3232'
	    while (orderdetails.previous()) {
		String productCode = orderdetails.getString("productCode");
		int orderNumber = orderdetails.getInt("orderNumber");

		if (productCode.equals("S18_3232")) {
		    System.out.println("Order Number: " + orderNumber + "\n" +
				       "Product Code: " + productCode + "\n" );
		    break;
		}
	    }

	    // This section (and the class MSRPCompare) is analagous to the
	    // ABL code:
	    // FOR EACH products WHERE products.quantityInStock > 5000 
	    //    BY products.MSRP DESCENDING:
	    //    DISPLAY products. 
	    // END.

	    // Easiest way to sort in Java is by using Collections, so we'll 
	    // have to convert our result set into an ArrayList.

	    // Reset our tables
	    products.beforeFirst();
	    orderdetails.beforeFirst();

	    // Iterate through the result set, filtering out 
	    // the unwanted ones. 
	    while (products.next()) {
		if (products.getInt("quantityInStock") > 5000) {
		    // This ArrayList represents a row
		    ArrayList<String> row = new ArrayList<String>();

		    // Add each column to our ArrayList
		    row.add(products.getString("productCode"));
		    row.add(products.getString("productName"));
		    row.add(products.getString("productLine"));
		    row.add(products.getString("productScale"));
		    row.add(products.getString("productVendor"));
		    row.add(products.getString("productDescription"));
		    row.add(Double.toString(products.getDouble("MSRP")));

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

 	    // This section is analogous to the ABL code:
	    // FOR EACH orderdetails WHERE orderdetails.quantity >= 100,
	    //     EACH products WHERE products.productCode = orderdetails.productCode:
	    //     DISPLAY products.productName products.productCode orderdetails.quantityOrdered.
	    // END.

	    // Reset our tables. 
	    products.beforeFirst();
	    orderdetails.beforeFirst();

	    while (products.next()) {
		String productCode = products.getString("productCode"); 
		String productName = products.getString("productName"); 

		while (orderdetails.next()) {
		    String orderProductCode = orderdetails.getString("productCode");
		    int quantityOrdered = orderdetails.getInt("quantityOrdered");
		    
		    if (productCode.equals(orderProductCode) && 
			quantityOrdered >= 50) {
			System.out.println("Product Code: " + productCode + "\n" +
					   "Product Name: " + productName + "\n" +
					   "Quantity Ordered: " + quantityOrdered + "\n");
		    }
		}
	    }    

	    // This section is analagous to the ABL code:
	    // FOR EACH products:
	    //     FOR EACH orderdetails WHERE orderdetails.productCode = products.productCode:
	    //         ACCUMULATE orderdetails.orderNumber (COUNT).
	    //         ACCUMULATE orderdetails.quantityOrdered (TOTAL).
	    //     END.
	    //     DISPLAY products.productName (ACCUM COUNT orderdetails.orderNumber) 
	    //         (ACCUM TOTAL orderdetails.quantityOrdered).
	    // END.

	    // Reset our tables. 
	    products.beforeFirst();
	    orderdetails.beforeFirst();

	    while (products.next()) {
		String productCode = products.getString("productCode"); 

		int orderCount = 0;
		double orderTotal = 0; 

		orderdetails.beforeFirst();
		while (orderdetails.next()) {
		    String orderProductCode = orderdetails.getString("productCode");
		    double priceEach = orderdetails.getDouble("priceEach");
		    int quantityOrdered = orderdetails.getInt("quantityOrdered");
		    if (productCode.equals(orderProductCode)) {
			orderCount += 1;
			orderTotal += priceEach * quantityOrdered;
		    }
		}

		System.out.println("Product Code: " + productCode + "\n" +
				   "Number of Times Ordered: " + orderCount + "\n" +
				   "Total Profit: " + orderTotal + "\n");
	    }	 	    
	} 
	// Handle exceptions
	catch(Exception e){
	    System.out.println(e.getMessage());
	} 
	// Close resources
	finally {
	    try {
		stmt1.close();
		stmt2.close();
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