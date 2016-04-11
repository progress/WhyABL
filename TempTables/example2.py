import MySQLdb as mdb

class TempTable:
    customerNumber = 0
    salesRep = 0

with open('input.txt', 'r') as f:
    lines = f.readlines()

temptable = []

for line in lines:
    words = line.split()

    tt = TempTable()
    
    tt.customerNumber = int(words[0])
    tt.salesRep = int(words[1])

    temptable.append(tt)

try:
    
    db = mdb.connect('localhost', 'root', '', 'classicmodels')
    cur = db.cursor()

    for tt in temptable:
        cur.execute("UPDATE customers " +  \
                    "SET salesRepEmployeeNumber = " + \
                    "(SELECT employeeNumber FROM employees WHERE employeeNumber = {}) ".format(tt.salesRep) + \
                    "WHERE customerNumber = {}".format(tt.salesRep))

    db.commit()
    print "{} rows updated".format(cur.rowcount)
except Exception as e:
    db.rollback()
    print e
finally:
    if cur:
        cur.close()
    if db:
        db.close()
