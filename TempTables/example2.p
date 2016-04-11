DEFINE temp-table primes
   FIELD id like customer.customerNumber
   FIELD repName like salesRep.repName
END.

// Read in the numbers from a file

FOR EACH prime:
   FIND Customer WHERE Customer.CustNum = prime.id.
   FIND SalesRep WHERE SalesRep.RepName = prime.repName.
   Customer.SalesRep = SalesRep.SalesRep.
END.
