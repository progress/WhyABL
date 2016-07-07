# Why ABL Example
# Authors: Bill Wood, Alan Estrada
# File Name: DbAndStructures/example.py
# Version 1.05

import MySQLdb

with open('input.txt', 'r') as f:
    lines = f.readlines()

primeCustomers = []

for line in lines:
    words = line.split()
    primeCustomers.append({"id": int(words[0]), "newRep": words[1]})

try:
    db = MySQLdb.connect('localhost', 'root', '', 'sports2000')
    cur = db.cursor()

    # Get a list of all the customers we are going to impact
    # This section is analagous to the ABL code:
	# FOR EACH primeCustomer, FIRST Customer
    #   WHERE Customer.CustNum = primeCustomer.id BY primeCustomer.id:
    #   DISPLAY Customer.Name primeCustomer.id.
    # END.

    # Create a string to use for the SQL IN operator
    customerNumbers = '('
    customerNumbers += ", ".join([p['id'] for p in primeCustomers])
    customerNumbers += ')'

    sql = ('SELECT Name, custNum FROM customers'
           ' WHERE custNum IN %s')
    cur.execute(sql, (customerNumbers,))

    row = cur.fetchone()
    while row is not None:
        print ", ".join([str(c) for c in row])
        row = cur.fetchone()

    # Get a list of all the new salesReps for Massachusetts customers
    # This is analagous to the ABL code:
    # FOR EACH Customer WHERE Customer.State = "MA",
    #    EACH primeCustomer WHERE primeCustomer.id = Customer.CustNum:
    #    DISPLAY Customer.Name primeCustomer.newRep.
    # END.
    sql = "SELECT Name, custNum from customers where State = 'MA'"
    cur.execute(sql)

    row = cur.fetchone()
    while row is not None:
        for p in primeCustomers:
            if row[1] == p['id']:
                print row[0], p['newRep']

        row = cur.fetchone()

    # Go through each customer and assign them the sales rep
    # with the given name
    #
    # This is analagous to the ABL code:
    # FOR EACH prime:
    #    FIND Customer WHERE Customer.CustNum = primeCustomer.customerNumber.
    #    FIND SalesRep WHERE SalesRep.RepName = primeCustomer.newRep.
    #    Customer.SalesRep = SalesRep.SalesRep.
    # END.
    for p in primeCustomers:
        sql = ('UPDATE customer'
               'SET salesRep = '
               '(SELECT salesRep FROM SalesRep WHERE repName = "%s" LIMIT 1) '
               'WHERE customerNumber = %s')
        cur.execute(sql, (p['newRep'], p['id']))

    db.commit()
except Exception as e:
    db.rollback()
    print e
finally:
    if cur:
        cur.close()
    if db:
        db.close()
