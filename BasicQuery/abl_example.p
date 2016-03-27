// Why ABL Example
// Authors: Bill Wood, Alan Estrada
// File Name: BasicQuery/abl_example.p
// Version 1.02
// 
// This .p shows how simple it is to make queries in ABL with very little
// redundancies and boilerplate code.

// The ABL compiler ensures datatypes match the schema.
DEFINE VARIABLE repname LIKE Customer.SalesRep INITIAL "GPE".
DEFINE VARIABLE maxBalance LIKE Customer.Balance INITIAL 200000.

FOR EACH Customer WHERE Customer.SalesRep = repname:
    IF Balance > CreditLimit THEN Balance = Balance + 5.
    IF Balance > maxBalance THEN Discount = 0.
END.
