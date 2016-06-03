# Why ABL Example
# Authors: Bill Wood, Alan Estrada
# File Name: DbAndStructures/example.py
# Version 1.04

import MySQLdb as mdb

class PrimeCustomer:
    id = 0
    newRep = ""

with open('input.txt', 'r') as f:
    lines = f.readlines()

primeCustomers = []

for line in lines:
    words = line.split()

    tt = PrimeCustomer()
    
    tt.id = int(words[0])
    tt.newRep = words[1]

    primeCustomers.append(tt)

try:
    db = mdb.connect('localhost', 'root', '', 'sports2000')
    cur = db.cursor()

    # Get a list of all the customers we are going to impact
    # This section is analagous to the ABL code:
	# FOR EACH primeCustomer, FIRST Customer WHERE Customer.CustNum = primeCustomer.id BY primeCustomer.id:
	#   DISPLAY Customer.Name primeCustomer.id.
	# END.
    
    # Create a string to use for the SQL IN operator
    customerNumbers = '('
    for prime in primeCustomers:
        customerNumbers += str(prime.id)
        customerNumbers += ', '
        
    # Remove the trailing comma and space and add ending parenthesis 
    customerNumbers = customerNumbers[:-2]
    customerNumbers += ')'

    cur.execute("SELECT Name, custNum from customers where custNum in {}".format(customerNumbers))
    
    row = cur.fetchone()
    while row is not None:
        print ", ".join([str(c) for c in row])
        row = cur.fetchone()

    # Get a list of all the new salesReps for Massachusetts customers
    # This is analagous to the ABL code:
    # FOR EACH Customer WHERE Customer.State = "MA", EACH primeCustomer WHERE primeCustomer.id = Customer.CustNum:
    #    DISPLAY Customer.Name primeCustomer.newRep.
    # END.
    cur.execute("SELECT Name, custNum from customers where State = 'MA'")
    
    row = cur.fetchone()
    while row is not None:
	for prime in primeCustomers:
		if row[1] == prime.id:
			print row[0], prime.newRep
		
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
    for prime in primeCustomers:
        cur.execute("UPDATE customer " +  \
                    "SET salesRep = " + \
                    "(SELECT salesRep FROM SalesRep WHERE repName = '{}' LIMIT 1) ".format(prime.newRep) + \
                    "WHERE customerNumber = {}".format(prime.id))

    db.commit()
except Exception as e:
    db.rollback()
    print e
finally:
    if cur:
        cur.close()
    if db:
        db.close()
