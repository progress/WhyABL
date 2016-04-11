/* Why ABL Example
   Authors: Bill Wood, Alan Estrada
   File Name: DBandStructures/example.p
   Version 1.01
*/

DEFINE temp-table primes
   FIELD id like customer.CustNum
   FIELD newRep like salesRep.RepName.
END.

// Read in the numbers from a file

/* Case 1: Get a list of all the Customers we are going to impact. */
FOR EACH prime, FIRST Customer WHERE Customer.CustNum = prime.customerNumber BY prime.customerNumber:
   DISPLAY prime.customerNumber Customer.Name.
END.

/* Case 2: After being given a list of customers who have moved to a new region,
   go through each moved customer and assign them the sales rep of that
   region. */
FOR EACH prime:
   FIND Customer WHERE Customer.CustNum = prime.customerNumber.
   FIND SalesRep WHERE SalesRep.RepName = prime.newRep.
   Customer.SalesRep = SalesRep.SalesRep.
END.
