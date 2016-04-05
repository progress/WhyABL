# Why ABL Example
# Authors: Bill Wood, Alan Estrada
# File Name: BasicQuery/py_example.py
# 
# This is the Python equivalent of this slice of ABL code:
# FOR EACH Customer WHERE Customer.SalesRep = repname AND Balance > maxBalance:
#     Discount = 0.
# END.

import MySQLdb as mdb

salesrep = 1702
creditFactor = 1.05

# Python's try block implicitly gives transactional scoping
try:
    db = mdb.connect('localhost', 'root', '', 'classicmodels')
    
    # Create a cursor object to keep track of result set
    cur = db.cursor()

    # Execute the query
    cur.execute("UPDATE customers \
                 SET BALANCE = BALANCE * %s \
                 WHERE SALESREPEMPLOYEENUMBER = %s AND BALANCE > CREDITLIMIT",
                (creditFactor, salesRep))
    
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
