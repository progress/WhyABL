/* Why ABL Example
   Authors: Bill Wood, Alan Estrada
   File Name: DbAndStructures/example.p
   Version 1.04
   
   This example is to show the simplicity of being able to use temp-tables
   and be able to query temp-tables as if they were actual tables in the
   database. This is useful if you want to do joins from data that you
   read from a file and not necessariy want to put in a database table.
*/

DEFINE temp-table primeCustomer
   FIELD id LIKE customer.CustNum
   FIELD newRep LIKE salesrep.repname.

DEFINE VARIABLE i1 as INTEGER.
DEFINE VARIABLE c1 as CHARACTER.

// Read in information from a file
INPUT FROM "input.txt".

REPEAT :
    IMPORT  i1 c1.
    CREATE primeCustomer.
    
    primeCustomer.id = i1.
    primeCustomer.newRep = c1.

END.

/* Case 1: Get a list of all the Customers we are going to impact. */
FOR EACH primeCustomer, FIRST Customer WHERE Customer.CustNum = primeCustomer.id BY primeCustomer.id:
   DISPLAY Customer.Name primeCustomer.id.
END.

/* Case 2: Get a list of all the new SalesReps for Massachusetts customers */
FOR EACH Customer WHERE Customer.State = "MA", EACH primeCustomer WHERE primeCustomer.id = Customer.CustNum:
   DISPLAY Customer.Name primeCustomer.newRep.
END.

/* Case 3: Go through each customer and assign them the sales rep
   with the given name. */
FOR EACH primeCustomer:
   FIND Customer WHERE Customer.CustNum = primeCustomer.id.
   FIND SalesRep WHERE SalesRep.RepName = primeCustomer.newRep.
   Customer.SalesRep = SalesRep.SalesRep.
END.
