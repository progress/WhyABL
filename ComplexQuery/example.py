# Why ABL Example
# Authors: Bill Wood, Alan Estrada
# File Name: ComplexQuery/example.py
# Version 1.05
#
# This is the Python equivalent of ComplexQuery/example.p

import MySQLdb

# We might potentially get this from a file or user input
shipDate = '1999-09-19'

try:
    con = MySQLdb.connect('localhost', 'root', '', 'sports2000')
    cur = con.cursor(MySQLdb.cursors.DictCursor)

    # Join on 4 tables
    # We do an join the Employees, Customers, Orders, and OrderDetails tables
    # and then sort out all the orders where we are processing orders and the 
    # order contains a specific product
    sql = ("SELECT * FROM salesrep JOIN customer "
           "ON customer.salesRep = salesrep.salesrep "
           "JOIN order ON order.custNum = customer.custNum "
           "JOIN orderline ON orderline.ordernum = order.orderNum "
           "WHERE order.OrderStatus = 'Ordered' OR "
           "(order.orderStatus = 'Shipped' AND "
           "ABS(DATEDIFF(order.shipDate, %s)) <= 31) "
           "AND orderline.itemnum = 14;")

    cur.execute(sql, (shipDate,))

    rows = cur.fetchall()

    for row in rows:
        print "Rep Name: ", row['repName']
        print "Customer Name: ", row['Name']
        print "Order Number: ", row['ordernum']
        print "Price: ", row['price']

    print "{} rows found".format(cur.rowcount)
except:
    print "Query failed"
finally:
    if cur:
        cur.close()
    if con:
        con.close()
