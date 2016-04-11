import sys

from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, String, Integer, Text, ForeignKey, desc,\
    create_engine, func
from sqlalchemy.dialects.mysql import DOUBLE, SMALLINT
from sqlalchemy.orm import sessionmaker, relationship
from sqlalchemy.exc import SQLAlchemyError


# Create the mappings
Base = declarative_base()

# The object to represent a record in the 'products' table 
class Products(Base):
    __tablename__ = 'products'

    productCode = Column(String(15), primary_key=True)
    productName = Column(String(70))
    productLine = Column(String(50))
    productScale = Column(String(10))
    productVendor = Column(String(50))
    productDescription = Column(Text())
    quantityInStock = Column(SMALLINT(6))
    buyPrice = Column(DOUBLE())
    MSRP = Column(DOUBLE())

    def __repr__(self):
        return """<Product(productCode: {}, productName: {}, productLine: {},     
        ProductScale: {}, productVendor: {}, productDescription: {},
        quantityInStock: {}, buyPrice: {}, MSRP: {}""".format(self.productCode,
                                                              self.productName,
                                                              self.productLine,
                                                              self.productScale,
                                                              self.productVendor,
                                                              self.productDescription,
                                                              self.quantityInStock,
                                                              self.buyPrice,
                                                              self.MSRP)

# The object to represent a record in the 'orderdetails' table 
class OrderDetails(Base):
    __tablename__ = 'orderdetails'

    orderNumber = Column(Integer, primary_key=True)
    productCode = Column(String(15), ForeignKey('products.productCode'))
    quantityOrdered = Column(Integer)
    priceEach = Column(DOUBLE())
    orderLineNumber = Column(SMALLINT(6))

    products = relationship("Products")

    def __repr__(self):
        return """orderNumber: {}, productCode: {}, quantityOrdered: {}, 
        priceEach: {}, orderLineNumber: {}""".format(self.orderNumber,
                                                     self.productCode,
                                                     self.quantityOrdered,
                                                     self.priceEach,
                                                     self.orderLineNumber)

try:
    # Create an engine that connects us to the database
    engine = create_engine("mysql://root@localhost/classicmodels")
        
    # Create a session that acts as the intermediary between the database
    # and local changes
    Session = sessionmaker(bind=engine)
    session = Session() 
    
    # Query the database for all the records in 'produts' and 'orderdetails'
    # and put them in respective query objects
    products = session.query(Products)
    orderdetails = session.query(OrderDetails)

    # This section is analagous to the ABL code:
    # FIND FIRST product where productVendor = Classic Metal Creations.
    query = products.filter(Products.productVendor == 'Classic Metal Creations')
    print query[0]

    # This section is analagous to the ABL code:
    # FIND LAST orderdetails where productCode = 'S18_3232'.    
    index = -1 
    print query[index]

    # This section is analagous to the ABL code:
    # FIND PREV orderdetails where productCode = 'S18_3232'
    index += -1
    print query[index]

    # This section (and the class MSRPCompare) is analagous to the
    # ABL code:
    # FOR EACH products WHERE products.quantityInStock > 5000 
    #    BY products.MSRP DESCENDING:
    #    DISPLAY products. 
    # END.

    for product in products.filter(Products.quantityInStock > 5000).\
        order_by(Products.MSRP.desc()):
        print product

    # This section is analogous to the ABL code:
    # FOR EACH orderdetails WHERE orderdetails.quantityOrdered >= 50,
    #     EACH products WHERE products.productCode = orderdetails.productCode:
    #     DISPLAY products.productName products.productCode orderdetails.quantityOrdered.
    # END.

    for o, p in orderdetails.join(Products, Products.productCode == OrderDetails.productCode)\
                            .with_entities(OrderDetails, Products).\
                            filter(OrderDetails.quantityOrdered >= 50):
        print p.productName, p.productCode, o.quantityOrdered


    # This section is analagous to the ABL code:
    # FOR EACH products:
    #     FOR EACH orderdetails WHERE orderdetails.productCode = products.productCode:
    #         ACCUMULATE orderdetails.orderNumber (COUNT).
    #         ACCUMULATE orderdetails.quantityOrdered (TOTAL).
    #     END.
    #     DISPLAY products.productName (ACCUM COUNT orderdetails.orderNumber) 
    #         (ACCUM TOTAL orderdetails.quantityOrdered).
    # END.

    for productCode, numOrders, quantityOrdered in orderdetails.with_entities(
            OrderDetails.productCode, 
            func.count(OrderDetails.productCode),
            func.sum(OrderDetails.quantityOrdered)).group_by(OrderDetails.productCode):
        print productCode, numOrders, quantityOrdered
   
except SQLAlchemyError, e:  
    print e
    sys.exit(1)

finally:
    if session:
        session.close()
