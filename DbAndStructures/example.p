/* Why ABL Example
   Authors: Bill Wood, Alan Estrada
   File Name: DbAndStructures/example.p
   Version 1.02
*/

// TODO: Change to primeCustomer
DEFINE temp-table primes
   FIELD id like customer.CustNum
   FIELD newRep like salesRep.RepName.
END.

// Read in the numbers from a file

/* Case 1: Get a list of all the Customers we are going to impact. */
FOR EACH prime, FIRST Customer WHERE Customer.CustNum = prime.id BY prime.id:
   DISPLAY prime.customerNumber Customer.Name.
END.

/* Case 2: Get a list of all the new SalesReps for Massachusetts customers */
FOR EACH Customer WHERE Customer.State = "MA", EACH prime WHERE prime.id = Customer.CustNum:
   DISPLAY Customer.Name prime.newRep.
END.

/* Case 3: Go through each customer and assign them the sales rep
   with the given name. */
FOR EACH prime:
   FIND Customer WHERE Customer.CustNum = prime.customerNumber.
   FIND SalesRep WHERE SalesRep.RepName = prime.newRep.
   Customer.SalesRep = SalesRep.SalesRep.
END.
