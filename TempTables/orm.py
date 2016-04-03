import sys

from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, String, Integer, Text, ForeignKey
from sqlalchemy.dialects.mysql import DOUBLE, SMALLINT
from sqlalchemy.orm import sessionmaker, relationship
import sqlalchemy


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
    productDescription = Text()
    quantityInStock = SMALLINT(6)
    buyPrice = DOUBLE()
    MSRP = DOUBLE()

# The object to represent a record in the 'orderdetails' table 
class OrderDetails(Base):
    __tablename__ = 'orderdetails'

    orderNumber = Column(Integer, primary_key=True)
    productCode = Column(String(15), ForeignKey('products.productCode'))
    quantityOrdered = Column(Integer)
    priceEach = DOUBLE()
    orderLineNumber = SMALLINT(6)

    products = relationship("Products")

try:
    # Create an engine that connects us to the database
    engine = sqlalchemy.create_engine("mysql://root@localhost/classicmodels")
        
    # Create a session that acts as the intermediary between the database
    # and local changes
    Session = sessionmaker(bind=engine)
    session = Session() 
    
    # Query the database for all the records in 'produts' and 'orderdetails'
    products = session.query(Products)
    orderdetails = session.query(OrderDetails)

    for product in products:
        print product.productCode, product.productName

    for detail in orderdetails:
        print detail.productCode, detail.orderNumber
 
    session.close()

    print "Goodbye!"
   
except sqlalchemy.exc.SQLAlchemyError, e:  
    print e
    sys.exit(1)

finally:
    if session:
        session.close()
