/* Why ABL Example
   Authors: Bill Wood, Alan Estrada
   File Name: ComplexQuery/example.p
   Version 1.04
 
   This .p shows how simple complex joins can be when writen in OpenEdge ABL.
*/

DEF VAR shipDate AS DATE INITIAL 09/19/1999.

FOR EACH salesrep,
    EACH customer OF salesrep,
    // An alternate version of this statement is:
    // EACH customer WHERE cust.salesrep = salesrep.salesrep,
        EACH order OF customer WHERE Order.OrderStatus = "Ordered" OR
        (Order.OrderStatus = "Shipped" AND INTERVAL(order.shipdate, shipDate, "months") <= 1),
            EACH orderline OF order WHERE orderline.itemnum = 14:
                // You can access any field from the joined record.
                DISPLAY 
                    SalesRep.Repname FORMAT "x(10)"
                    Customer.NAME
                    order.ordernum 
                    orderline.price.
END.
