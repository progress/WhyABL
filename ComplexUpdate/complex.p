// Why ABL Example
// Authors: Bill Wood, Alan Estrada
// File Name: ComplexUpdate/complex.p
// Version 11.6.1
// 
// This .p shows the usefulness of ABL transactions
// in complex database manipulations.

DEFINE VARIABLE id AS INTEGER INITIAL 0.
DEFINE VARIABLE ref-cust-num AS INTEGER INITIAL 0.

DEFINE BUFFER ref-cust FOR Customer.
DEFINE BUFFER ord1 FOR order.
DEFINE BUFFER ord2 FOR order.

// Can be replaced with name or any other field.
id = 3010.
ref-cust-num = 1010. 

DO  TRANSACTION:
    FIND FIRST customer WHERE customer.custnum = id NO-ERROR.
    
    IF NOT available(customer) THEN DO:
        CREATE customer.
        customer.custnum = id.
        // Fill in customer fields.
    END.

    // Fill in order fields.
    CREATE order.
    order.custnum = customer.custnum.
    
    // Fill in orderline fields.
    CREATE orderline.
    orderline.ordernum = order.ordernum.
    
    // Give a $50 credit to the referring customer.
    IF ref-cust-num > 0 THEN DO:
        FIND FIRST ref-cust WHERE ref-cust.custnum = ref-cust-num NO-ERROR.                                            
    
        IF available(ref-cust) THEN DO:
            ref-cust.balance = ref-cust.balance - 50.
        END.
    END.

    // Loop through all the records
    FOR EACH ord1 WHERE ord1.custnum = customer.custnum:
        
        // Find orders that happen in the same month
        // and assign them the same ship date.
        FIND FIRST ord2 WHERE 
            MONTH(ord1.shipdate) = MONTH(ord2.shipdate) AND 
            YEAR(ord1.shipdate) = YEAR(ord2.shipdate) NO-ERROR.
        
        IF available(ord2) THEN DO: 
            IF ord2.shipdate < ord1.shipdate THEN DO:
                ord2.shipdate = ord1.shipdate.
            END.
            ELSE DO: 
                ord1.shipdate = ord2.shipdate.  
            END.

            // Maybe decrease their balance to save
            // on shipping here.
        END.

    END.
END.
