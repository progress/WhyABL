// Why ABL Example
// Authors: Bill Wood, Alan Estrada
// File Name: BasicQuery/dotnet_example.cs
// Version 11.6.1
// 
// This is the C# equivalent of this slice of ABL code:
//
// FOR EACH Customer WHERE Customer.SalesRep = repname:
//     IF Balance > CreditLimit THEN Balance = Balance + 5.
// END.

using System;

using MySql.Data;
using MySql.Data.MySqlClient;

// Note that if you DO not give this a data source, MySql namespaces will NOT 
// be resolved properly.
namespace ConsoleApplication1
{
    class Program
    {
        static void Main(string[] args)
        {
            string connStr = "server=localhost;user=root;database=classicmodels;port=3306;password=password;";
            MySqlConnection conn = new MySqlConnection(connStr);
            MySqlTransaction tr = null;

            int salesRep = 1702;
            try
            {
                // Open the connection to the database
                conn.Open();

                String update = "UPDATE customers " +
                    "SET BALANCE = BALANCE + 0.05 " +
                    "WHERE SALESREPEMPLOYEENUMBER = @salesRep AND BALANCE > CREDITLIMIT";

                // Begin a transaction
                tr = conn.BeginTransaction();

                // Build the query
                MySqlCommand cmd = new MySqlCommand();
                cmd.Connection = conn;
                cmd.Transaction = tr;

                cmd.CommandText = update;
                cmd.Parameters.AddWithValue("@salesRep", salesRep);

                // Execute the query and commit it if we don't throw anything
                cmd.ExecuteNonQuery();
                tr.Commit();
            }
            catch (MySqlException ex)
            {
                try
                {
                    tr.Rollback();
                }
                catch (MySqlException ex1)
                {
                    Console.WriteLine(ex1.ToString());
                }

                Console.WriteLine(ex.ToString());
            }
            finally
            {
                if (conn != null)
                {
                    conn.Close();
                }
            }
        }
    }
}
