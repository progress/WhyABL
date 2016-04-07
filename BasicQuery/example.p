/* Why ABL Example
   Authors: Bill Wood, Alan Estrada
   File Name: BasicQuery/example.p
   Version 1.03
 
   This .p shows how simple it is to make queries in ABL with very little
   redundancies and boilerplate code.
*/

/* The ABL compiler ensures datatypes match the schema.*/
DEFINE VARIABLE repname LIKE Customer.SalesRep INITIAL "GPE".
DEFINE VARIABLE creditFactor LIKE Customer.Balance INITIAL 1.05.

FOR EACH Customer WHERE SalesRep = repname AND Balance > CreditLimit:
   Balance = Balance * creditFactor.
END.
