# Why ABL Example
# Authors: Bill Wood, Alan Estrada
# File Name: BasicQuery/example2.py
 
import MySQLdb as mdb

class TempTable:
    customerNumber = 0
    email = 0

with open('input.txt', 'r') as f:
    lines = f.readlines()

temptable = []

for line in lines:
    words = line.split()

    tt = TempTable()
    
    tt.customerNumber = int(words[0])
    tt.email = int(words[1])

    temptable.append(tt)

try:
    db = mdb.connect('localhost', 'root', '', 'classicmodels')
    cur = db.cursor()

    # After being given a list of customers and the emails of their new sales reps,
    # iterate through each customer and assign them the sales rep that the email
    # belongs to.
    #
    # This is analagous to the ABL code:
    # FOR EACH tt:
    #    FIND Customers WHERE Customers.customerNumber = tt.customerNumber.
    #    FIND employees WHERE employees.email = tt.email.
    #    Customers.salesRepEmployeeNumber = employees.employeeNumber.
    # END.
    for tt in temptable:
        cur.execute("UPDATE customers " +  \
                    "SET salesRepEmployeeNumber = " + \
                    "(SELECT employeeNumber FROM employees WHERE email = {}) ".format(tt.email) + \
                    "WHERE customerNumber = {}".format(tt.customerNumber))

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
