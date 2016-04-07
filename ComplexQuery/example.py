# Why ABL Example
# Authors: Bill Wood, Alan Estrada
# File Name: ComplexQuery/example.py
# Version 1.03
# 
# This is the Python equivalent of this slice of ABL code:
#
# FOR EACH employee,
#    EACH customers OF employee,
#        EACH orders OF customers WHERE orders.orderStatus = "In Process" OR
#        (orders.orderStatus = "Shipped" AND INTERVAL(orders.shippedDate, shipDate, "days") <= 31),
#            EACH orderdetail OF orders WHERE orderdetails.productCode = "S24_2300":
#                DISPLAY 
#                    employees.employeeNumber
#                    customers.customerName
#                    orders.orderNumber 
#                    orderdetails.priceEach.
# END.

import MySQLdb as mdb

shipDate = '2005-04-07'

try:
    con = mdb.connect('localhost', 'root', '', 'classicmodels')
    cur = con.cursor(mdb.cursors.DictCursor)

    # Join on 4 tables
    # We do an join the Employees, Customers, Orders, and OrderDetails tables
    # and then sort out all the orders where we are processing orders and the order
    # contains a specific product
    sql = "SELECT * FROM employees JOIN customers ON customers.salesRepEmployeeNumber = employees.employeeNumber " + \
          "JOIN orders ON orders.customerNumber = customers.customerNumber " + \
          "JOIN orderdetails ON orderdetails.orderNumber = orders.orderNumber " + \
          "WHERE (orders.status = 'In Process' OR ABS(DATEDIFF(orders.shippedDate, {})) <= 31) ".format(shipDate) + \
          "AND orderdetails.productCode = 'S24_2300';"

    cur.execute(sql)

    rows = cur.fetchall()

    for row in rows:
        print "Employee Number: ", row['employeeNumber']
        print "Customer Name: ", row['customerName']
        print "Order Number: ", row['orderNumber']
        print "Price Each: ", row['priceEach']

    print "{} rows found".format(cur.rowcount)
except:
    print "Query failed"
finally:
    if cur:
        cur.close()
    if con:
        con.close()
