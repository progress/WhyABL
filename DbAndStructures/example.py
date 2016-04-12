# Why ABL Example
# Authors: Bill Wood, Alan Estrada
# File Name: DbAndStructures/example.py
# Version 1.02
#

import MySQLdb as mdb

# TODO: Change to primeCustomers (don't use TempTable).
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
    tt.email = words[1]

    temptable.append(tt)

try:
    db = mdb.connect('localhost', 'root', '', 'classicmodels')
    cur = db.cursor()

    # Case 1: Get a list of all the customers we are going to impact
    # This section is analagous to the ABL code:
    # FOR EACH tt:
    #   FIND FIRST customers WHERE customers.customerNumber = tt.customerNumber BY tt.customerNumber.
    #   DISPLAY tt.customerNumber customers.Name.
    # END.
    
    # Create a string to use for the SQL IN operator
    customerNumbers = '('
    for tt in temptable:
        customerNumbers += str(tt.customerNumber)
        customerNumbers += ', '
        
    # Remove the trailing comma and space and add ending parenthesis 
    customerNumbers = customerNumbers[:-2]
    customerNumbers += ')'

    cur.execute("SELECT customerNumber, customerName from customers where customerNumber in {}".format(customerNumbers))
    
    row = cur.fetchone()
    while row is not None:
        print ", ".join([str(c) for c in row])
        row = cur.fetchone()

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
                    "(SELECT employeeNumber FROM employees WHERE email = '{}' LIMIT 1) ".format(tt.email) + \
                    "WHERE customerNumber = {}".format(tt.customerNumber))

    db.commit()
except Exception as e:
    db.rollback()
    print e
finally:
    if cur:
        cur.close()
    if db:
        db.close()
