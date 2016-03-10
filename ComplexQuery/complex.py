# Why ABL Example
# Authors: Bill Wood, Alan Estrada
# File Name: ComplexQuery/complex.py
# Version 11.6.1
# 
# This is the Python equivalent of this slice of ABL code:
#
# FOR EACH salesrep,
#    EACH customer OF salesrep,
#        EACH order OF customer WHERE Order.OrderStatus = "Ordered" OR
#        (Order.OrderStatus = "Shipped" AND INTERVAL(order.shipdate, dt1, "months") <= 1),
#            EACH orderline OF order WHERE orderline.itemnum = 14:
#                DISPLAY 
#                    SalesRep.Repname FORMAT "x(10)"
#                    Customer.NAME
#                    order.ordernum 
#                    orderline.price.
# END.

import config 
import MySQLdb as mdb

try:
    db = mdb.connect('localhost', 'root', config.password, 'classicmodels')
    cur = db.cursor()

    # Join on 4 tables
    # We do an outer join the Employees, Customers, Orders, and OrderDetails tables
    # And then sort out all the orders where we are processing orders and the order
    # contains a specific product
    cur.execute('select \
                 employees.lastName, employees.firstName, customers.customerName, \
                 orders.orderNumber, orderdetails.priceEach \
                 from employees join customers on customers.salesRepEmployeeNumber = employees.employeeNumber \
                                join orders on orders.customerNumber = customers.customerNumber \
                                join orderdetails on orderdetails.orderNumber = orders.orderNumber \
                                where orders.status = "In Process" and orderdetails.productCode = "S24_2300";')
    
    print "{} rows found".format(cur.rowcount)
except:
    print "Query failed"
finally:
    if cur:
        cur.close()
    if db:
        db.close()
