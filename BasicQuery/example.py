# Why ABL Example
# Authors: Bill Wood, Alan Estrada
# File Name: BasicQuery/example.py
# Version 1.04 
#
# This is the Python equivalent of this slice of ABL code:
# FOR EACH Customer WHERE SalesRep = repname AND Balance > CreditLimit:
#    Balance = Balance * creditFactor.
# END.

import MySQLdb as mdb

repName = "GPE"
creditFactor = 1.05

# Python's try block implicitly gives transactional scoping
try:
    db = mdb.connect('localhost', 'root', '', 'sports2000')
    
    # Create a cursor object to keep track of result set
    cur = db.cursor()

    # Execute the query
    cur.execute("UPDATE customers \
                 SET BALANCE = BALANCE * %s \
                 WHERE SALESREPEMPLOYEENUMBER = %s AND BALANCE > CREDITLIMIT",
                (creditFactor, repName))
    
    # Commit the changes 
    db.commit()
    print "{} rows updated".format(cur.rowcount)
except Exception as e:
    db.rollback()
    print e
finally:
    if cur:
        cur.close()
    if db:
        db.close()
