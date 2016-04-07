/* Why ABL Example
   Authors: Bill Wood, Alan Estrada
   File Name: OperateOnResults/abl_example.p
   Version 1.02
 
   This .p shows how you can interact with the database as if it were native 
   to the language without copying data around.
*/

FOR EACH Customer WHERE Customer.SalesRep = "DOS":
   DISPLAY CustNum Name Balance.
END.

/* Alternate to match how this works with an output to a file or console. */
OUTPUT TO "report.txt".
FOR EACH Customer WHERE Customer.SalesRep = "DOS":
   PUT "ID: "  CustNum " Name: " Name " Balance: " Balance SKIP.
END.
OUTPUT CLOSE.
