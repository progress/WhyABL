// TODO: Figure out a decent name.

DEFINE TEMP-TABLE ttitem NO-UNDO LIKE ITEM.
DEFINE TEMP-TABLE ttorderline NO-UNDO LIKE ORDERLINE.

/* Create a working set of the ITEM temp-table that fulfill
   certain conditions. */
FOR EACH ITEM:
    IF ITEM.weight >= 2.0 THEN DO:
        CREATE ttitem.
        BUFFER-COPY ITEM TO ttitem.
        RELEASE ttitem.
    END.
END.

FOR EACH orderline:
    CREATE ttorderline.
    BUFFER-COPY ORDERLINE TO ttorderline.
    RELEASE ttorderline.
END.

/* Navigate to a specific index */
FIND FIRST ttitem WHERE ttitem.itemnum = 14.

/* Sort from heighest weight to lowest and filter out 
   items where we have stock less than 10000 */
FOR EACH ttitem WHERE ttitem.onhand > 10000 
    BY ttitem.weight DESCENDING:
    DISPLAY ttitem. 
END.

/* Find a subset of items that have been ordered more than 100 times
   in a single order. */
FOR EACH ttorderline WHERE ttorderline.qty >= 100,
    EACH ttitem WHERE ttorderline.itemnum = ttitem.itemnum:
    DISPLAY ttitem.itemname ttitem.itemnum ttorderline.qty.
END.

/* Find the number of orders each item had and sort by
   ttitem.name. */ 
FOR EACH ttitem BY ttitem.itemname:
    FOR EACH ttorderline WHERE ttorderline.itemnum = ttitem.itemnum:
         ACCUMULATE ttorderline.ordernum (COUNT).
    END.
    DISPLAY ttitem.itemname (ACCUM COUNT ttorderline.ordernum).
END.


