// Why ABL Example
// Authors: Bill Wood, Alan Estrada
// File Name: ComplexUpdate/example.java
// Version 1.04
//
// This example is equivalent to the ABL procedure ComplexUpdate/example.p

import java.sql.*;

public class example {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/sports2000";

    // Database credentials
    static final String USER = "root";
    static final String PASS = "";
   
    public static void main(String[] args) {
        PreparedStatement preparedStmt;
        Statement stmt;
        
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
            conn.setAutoCommit(false);

            stmt = conn.createStatement();
            
            // Find if customer exists
            int custNum = 3010;

            String sql = "SELECT * FROM customer where custNum = " + custNum;
            ResultSet rs = stmt.executeQuery(sql);
            
            // If customer doesn't exist, add the customer
            if (!rs.isBeforeFirst()) {  
                // Fill in fields with information
                sql = "INSERT INTO customers " +
                    "VALUES (?, 'Philippines', 'Susan Dune', " +
                    "'29 Fake St', 'Extinct City', 'MA', '01733', 'USA'," +
                    "null, '(555)555-5555', 'GPE', 10000, 0, null, 0, null, " +
                    "null, 'sdune@fake.com')";
                
                preparedStmt = conn.prepareStatement(sql);
                preparedStmt.setInt(1, custNum);
                preparedStmt.executeUpdate();
            }

            // Find if referring customer exists
            sql = "SELECT * FROM customer where custNum = " + referringCustNum;
            rs = stmt.executeQuery(sql);

            // If customer does exist, give a $50 credit
            if (rs.isBeforeFirst()) {  
                int referringCustNum = 1010;
    
                sql = "UPDATE customer " +
                    "SET balance = balance - 50 WHERE custNum = ?";
                
                preparedStmt = conn.prepareStatement(sql);
                preparedStmt.setInt(1, referringCustNum);
                preparedStmt.executeUpdate();
            }

            // Create order
            int orderNum = 20400; 
            
            sql = "INSERT INTO order " +
                "VALUES (?, ?, '2005-03-02', '2005-03-10'," +
                "null, 'Fedex', null, null, 'GPE', null, null, " +
                "'Ordered', 0, 'Visa')";
            preparedStmt = conn.prepareStatement(sql);
            
            preparedStmt.setInt(1, orderNum);            
            preparedStmt.setInt(2, custNum);
            preparedStmt.executeUpdate();

            // Create orderline
            int itemnum = 54; 
            
            sql = "INSERT INTO orderdetails VALUES (?, 1, ?, null, 1," +
                "0, null, 'Ordered')";
            
            preparedStmt = conn.prepareStatement(sql);
            
            preparedStmt.setInt(1, orderNum);
            preparedStmt.setString(2, itemnum);
            preparedStmt.executeUpdate();

            // Find all the orders of the customer
            sql = "SELECT * FROM orders where custNum = " + custNum;
            rs = stmt.executeQuery(sql); 

            // If the order of the customer is within a month of another order,
            // merge the two orders by combining their shipping dates.
            while (rs.next()) {
                java.sql.Date sqlDate = rs.getDate(2);
                sql = "SELECT * FROM order where custNum = ? AND " + 
                    "ABS(datediff(order.shipdate, ?)) <= 30";
                preparedStmt = conn.prepareStatement(sql);
                preparedStmt.setInt(1, custNum);
                preparedStmt.setDate(2, sqlDate);

                ResultSet rs2 = preparedStmt.executeQuery();

                if (rs2.next()) {  
                    java.sql.Date sqlDate2 = rs2.getDate(2);

                    sql = "UPDATE orders " +
                        "SET shipDate = ? WHERE orderNum = ?";	
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
        // Handle exceptions
        } catch(Exception e){
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
                if (stmt != null) {
                    stmt.close();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (preparedStmt != null) {
                    preparedStmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (rs != null) {
                    rs.close();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (rs2 != null) {
                    rs2.close();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
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
