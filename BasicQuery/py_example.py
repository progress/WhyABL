# Why ABL Example
# Authors: Bill Wood, Alan Estrada
# File Name: BasicQuery/py_example.py
# Version 11.6.1
# 
# This is the Python equivalent of this slice of ABL code:
#
# FOR EACH Customer WHERE Customer.SalesRep = repname:
#     IF Balance > CreditLimit THEN Balance = Balance + 5.
# END.

import config 
import MySQLdb as mdb

salesrep = raw_input("Enter the Sales Representative's Employee Number: ")

# Transactional scoping
try:
    db = mdb.connect('localhost', 'root', config.password, 'classicmodels')
    cur = db.cursor()
    cur.execute("UPDATE customers \
                 SET BALANCE = BALANCE + 0.05 \
                 WHERE SALESREPEMPLOYEENUMBER = %s AND BALANCE > CREDITLIMIT",
                (salesrep,))
    
    db.commit()
    print "{} rows updated".format(cur.rowcount)
except:
    db.rollback()
    print "Query failed"
finally:
    if cur:
        cur.close()
    if db:
        db.close()

