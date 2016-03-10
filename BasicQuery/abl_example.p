// Why ABL Example
// Authors: Bill Wood, Alan Estrada
// File Name: BasicQuery/abl_example.p
// Version 11.6.1
// 
// This .p shows how simple it is to make queries in ABL with very little
// redundancies and boilerplate code.

DEFINE VARIABLE repname AS CHARACTER INITIAL "Steve Buscemi".
    
FOR EACH Customer WHERE Customer.SalesRep = repname:
    IF Balance > CreditLimit THEN Balance = Balance + 5.
END.
