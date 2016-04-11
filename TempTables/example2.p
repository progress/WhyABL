/* Why ABL Example
   Authors: Bill Wood, Alan Estrada
   File Name: BasicQuery/example2.p
   Version 1.01
*/

DEFINE temp-table primes
   FIELD id like customer.customerNumber
   FIELD region like salesRep.region
END.

// Read in the numbers from a file

/* After being given a list of customers who have moved to a new region,
   go through each moved customer and assign them the sales rep of that
   region.
*/

FOR EACH prime:
   FIND Customer WHERE Customer.CustNum = prime.id.
   FIND SalesRep WHERE SalesRep.region= prime.region.
   Customer.SalesRep = SalesRep.SalesRep.
END.
