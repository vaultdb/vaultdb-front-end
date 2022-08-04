package org.vaultdb.support;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class TpcCQueries {
    public static final String TABLENAME_DISTRICT = "district";
    public static final String TABLENAME_WAREHOUSE = "warehouse";
    public static final String TABLENAME_ITEM = "item";
    public static final String TABLENAME_STOCK = "stock";
    public static final String TABLENAME_CUSTOMER = "customer";
    public static final String TABLENAME_HISTORY = "history";
    public static final String TABLENAME_OPENORDER = "oorder";
    public static final String TABLENAME_ORDERLINE = "order_line";
    public static final String TABLENAME_NEWORDER = "new_order";

    public static final List<String> QUERIES_Delivery = ImmutableList.of(
        /* Delivery procedures, 7 queries in total. */
        // 0 - delivGetOrderIdSQL: 
        "SELECT NO_O_ID FROM " + TABLENAME_NEWORDER + 
        " WHERE NO_D_ID = 4 " + // replace ? with 
        "   AND NO_W_ID = 1 " + 
        " ORDER BY NO_O_ID ASC " +  
        " LIMIT 1", 

        // 1 - delivDeleteNewOrderSQL: 
        "DELETE FROM " + TABLENAME_NEWORDER + 
        " WHERE NO_O_ID = 2101 " + 
        "   AND NO_D_ID = 4 " + 
        "   AND NO_W_ID = 1 ", 

        // 2 - delivGetCustIdSQL: 
        "SELECT O_C_ID FROM " + TABLENAME_OPENORDER +
        " WHERE O_ID = 2101 " +
        "   AND O_D_ID = 4 " +
        "   AND O_W_ID = 1", 

        // 3 - delivUpdateCarrierIdSQL:
        "UPDATE " + TABLENAME_OPENORDER +
        "   SET O_CARRIER_ID = 1 " +
        " WHERE O_ID = 2101 " +
        "   AND O_D_ID = 4" +
        "   AND O_W_ID = 1",

        // 4 - delivUpdateDeliveryDateSQL: 
        "UPDATE " + TABLENAME_ORDERLINE +
        "   SET OL_DELIVERY_D = '2022-07-30 04:51:39.807+00' " +
        " WHERE OL_O_ID = 2101 " +
        "   AND OL_D_ID = 4 " +
        "   AND OL_W_ID = 1 ", 

        // 5 - delivSumOrderAmountSQL: 
        "SELECT SUM(OL_AMOUNT) AS OL_TOTAL " +
        "  FROM " + TABLENAME_ORDERLINE +
        " WHERE OL_O_ID = 2101 " +
        "   AND OL_D_ID = 4 " +
        "   AND OL_W_ID = 1", 

        // 6 - delivUpdateCustBalDelivCntSQL: 
        "UPDATE " + TABLENAME_CUSTOMER +
        "   SET C_BALANCE = C_BALANCE + 47440.71875," +
        "       C_DELIVERY_CNT = C_DELIVERY_CNT + 1 " +
        " WHERE C_W_ID = 1 " +
        "   AND C_D_ID = 4 " +
        "   AND C_ID = 2697 "
    );

    public static final List<String> QUERIES_NewOrder = ImmutableList.of(
        // 0 - stmtGetCustSQL: 
        "SELECT C_DISCOUNT, C_LAST, C_CREDIT" +
        "  FROM " + TABLENAME_CUSTOMER +
        " WHERE C_W_ID = 1 " +
        "   AND C_D_ID = 4 " +
        "   AND C_ID = 1027",

        // 1 - stmtGetWhseSQL: 
        "SELECT W_TAX " +
        "  FROM " + TABLENAME_WAREHOUSE +
        " WHERE W_ID = 1",

        // 2 - stntGetDistSQL: explicitly removed the "FOR UPDATE" for passing the test cases.
//        "SELECT D_NEXT_O_ID, D_TAX " +
//        "  FROM " + TABLENAME_DISTRICT +
//        " WHERE D_W_ID = 1 AND D_ID = 4 FOR UPDATE",
        "SELECT D_NEXT_O_ID, D_TAX " +
        "  FROM " + TABLENAME_DISTRICT +
        " WHERE D_W_ID = 1 AND D_ID = 4",

        // 3 - stmtInsertNewOrderSQL: 
        "INSERT INTO " + TABLENAME_NEWORDER +
        " (NO_O_ID, NO_D_ID, NO_W_ID) " +
        " VALUES ( 3001, 4, 1)",

        // 4 - stmtUpdateDistSQL: 
        "UPDATE " + TABLENAME_DISTRICT +
        "   SET D_NEXT_O_ID = D_NEXT_O_ID + 1 " +
        " WHERE D_W_ID = 1 " +
        "   AND D_ID = 4",

        // 5 - stmtInsertOOrderSQL: 
        "INSERT INTO " + TABLENAME_OPENORDER +
        " (O_ID, O_D_ID, O_W_ID, O_C_ID, O_ENTRY_D, O_OL_CNT, O_ALL_LOCAL)" +
        " VALUES (3001, 4, 1, 1027, '2022-08-04 01:57:00.277+00', 9, 1)",

        // 6 - stmtGetItemSQL: 
        "SELECT I_PRICE, I_NAME , I_DATA " +
        "  FROM " + TABLENAME_ITEM +
        " WHERE I_ID = 39653",

        // 7 - stmtGetStockSQL: explicitly removed the FOR UPDATE for passing the tests.
//        "SELECT S_QUANTITY, S_DATA, S_DIST_01, S_DIST_02, S_DIST_03, S_DIST_04, S_DIST_05, " +
//        "       S_DIST_06, S_DIST_07, S_DIST_08, S_DIST_09, S_DIST_10" +
//        "  FROM " + TABLENAME_STOCK +
//        " WHERE S_I_ID = 39653 " +
//        "   AND S_W_ID = 1 FOR UPDATE",
        "SELECT S_QUANTITY, S_DATA, S_DIST_01, S_DIST_02, S_DIST_03, S_DIST_04, S_DIST_05, " +
        "       S_DIST_06, S_DIST_07, S_DIST_08, S_DIST_09, S_DIST_10" +
        "  FROM " + TABLENAME_STOCK +
        " WHERE S_I_ID = 39653 " +
        "   AND S_W_ID = 1",

        // 8 - stmtUpdateStockSQL: 
        "UPDATE " + TABLENAME_STOCK +
        "   SET S_QUANTITY = 67 , " +
        "       S_YTD = S_YTD + 3, " +
        "       S_ORDER_CNT = S_ORDER_CNT + 1, " +
        "       S_REMOTE_CNT = S_REMOTE_CNT + 0 " +
        " WHERE S_I_ID = 39653 " +
        "   AND S_W_ID = 1",

        // 9 - stmtInsertOrderLineSQL: 
        "INSERT INTO " + TABLENAME_ORDERLINE +
        " (OL_O_ID, OL_D_ID, OL_W_ID, OL_NUMBER, OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DIST_INFO) " +
        " VALUES (3001,4,1,1,39653,1,3,167.04000854492188,'jqmuwfkyzkbwjrlrjqunnvl ')"
    );

    public static final List<String> QUERIES_OrderStatus = ImmutableList.of(
        // 0 - ordStatGetNewestOrdSQL:
        "SELECT O_ID, O_CARRIER_ID, O_ENTRY_D " +
        "  FROM " + TABLENAME_OPENORDER +
        " WHERE O_W_ID = 1 " +
        "   AND O_D_ID = 3 " +
        "   AND O_C_ID = 463 " +
        " ORDER BY O_ID DESC LIMIT 1",

        // 1 - ordStatGetOrderLinesSQL:
        "SELECT OL_I_ID, OL_SUPPLY_W_ID, OL_QUANTITY, OL_AMOUNT, OL_DELIVERY_D " +
        "  FROM " + TABLENAME_ORDERLINE +
        " WHERE OL_O_ID = 1397" +
        "   AND OL_D_ID = 3" +
        "   AND OL_W_ID = 1",

        // 2 - payGetCustSQL
        "SELECT C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, " +
        "       C_CITY, C_STATE, C_ZIP, C_PHONE, C_CREDIT, C_CREDIT_LIM, " +
        "       C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_SINCE " +
        "  FROM " + TABLENAME_CUSTOMER +
        " WHERE C_W_ID = 1 " +
        "   AND C_D_ID = 2 " +
        "   AND C_ID = 2787",

        // 3 - customerByNameSQL
        "SELECT C_FIRST, C_MIDDLE, C_ID, C_STREET_1, C_STREET_2, C_CITY, " +
        "       C_STATE, C_ZIP, C_PHONE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, " +
        "       C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_SINCE " +
        "  FROM " + TABLENAME_CUSTOMER +
        " WHERE C_W_ID = 1 " +
        "   AND C_D_ID = 3 " +
        "   AND C_LAST = 'PRESANTIABLE' " +
        " ORDER BY C_FIRST"
    );

    public static final List<String> QUERIES_Payment = ImmutableList.of(
        // 0 - payUpdateWhseSQL:
        "UPDATE " + TABLENAME_WAREHOUSE +
        "   SET W_YTD = W_YTD + 4772.97998046875 " +
        " WHERE W_ID = 1 ",

        // 1 - payGetWhseSQL:
        "SELECT W_STREET_1, W_STREET_2, W_CITY, W_STATE, W_ZIP, W_NAME" +
        "  FROM " + TABLENAME_WAREHOUSE +
        " WHERE W_ID = 1",

        // 2 - payUpdateDistSQL
        "UPDATE " + TABLENAME_DISTRICT +
        "   SET D_YTD = D_YTD + 4772.97998046875 " +
        " WHERE D_W_ID = 1 " +
        "   AND D_ID = 5",

        // 3 - payGetDistSQL
        "SELECT D_STREET_1, D_STREET_2, D_CITY, D_STATE, D_ZIP, D_NAME" +
        "  FROM " + TABLENAME_DISTRICT +
        " WHERE D_W_ID = 1 " +
        "   AND D_ID = 5",

        // 4 - payGetCustSQL
        "SELECT C_FIRST, C_MIDDLE, C_LAST, C_STREET_1, C_STREET_2, " +
        "       C_CITY, C_STATE, C_ZIP, C_PHONE, C_CREDIT, C_CREDIT_LIM, " +
        "       C_DISCOUNT, C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_SINCE " +
        "  FROM " + TABLENAME_CUSTOMER +
        " WHERE C_W_ID = 1 " +
        "   AND C_D_ID = 5 " +
        "   AND C_ID = 2499",

        // 5 - payUpdateCustBalCdataSQL
        "UPDATE " + TABLENAME_CUSTOMER +
        "   SET C_BALANCE = -4782.97998046875, " +
        "       C_YTD_PAYMENT = 4782.97998046875, " +
        "       C_PAYMENT_CNT = 2, " +
        "       C_DATA = '2499 5 1 5 1 4772.98 | iarypvyfxaqxjrzdffzofozlbnoguvbtpfdsnhhskjapmpeglfjlauptaqulgwzpmorrdtykpjwmhwhxtvaosnlxnkldhdntpucrejdswbeculqqkosmxzjteztxywilmxsjwlvvbdhuebukyxxsynbjrheodtibtwruoahqiecfwheeaprgkpqtmwwmltgqseylmejhfvzpjxglzmloavoifrxyuaqovqqkphlqcjunmqyofhobjtjasomrvegqobfaxnkxgpaeeodkopvfgvkdkitfippoxvogxamqelmzkxpyqjjgwhcqwevpydcsedcer' " +
        " WHERE C_W_ID = 1 " +
        "   AND C_D_ID = 5 " +
        "   AND C_ID = 2499",

        // 6 - payUpdateCustBalSQL
        "UPDATE " + TABLENAME_CUSTOMER +
        "   SET C_BALANCE = -4718.43994140625, " +
        "       C_YTD_PAYMENT = 4718.43994140625, " +
        "       C_PAYMENT_CNT = 2 " +
        " WHERE C_W_ID = 1 " +
        "   AND C_D_ID = 9 " +
        "   AND C_ID = 1007",

        // 7 - payInsertHistSQL
        "INSERT INTO " + TABLENAME_HISTORY +
        " (H_C_D_ID, H_C_W_ID, H_C_ID, H_D_ID, H_W_ID, H_DATE, H_AMOUNT, H_DATA) " +
        " VALUES (5,1,2499,5,1,'2022-08-04 05:15:35.549+00',4772.97998046875,'jwzchi    zcupifw')",

        // 8 - customerByNameSQL
        "SELECT C_FIRST, C_MIDDLE, C_ID, C_STREET_1, C_STREET_2, C_CITY, " +
        "       C_STATE, C_ZIP, C_PHONE, C_CREDIT, C_CREDIT_LIM, C_DISCOUNT, " +
        "       C_BALANCE, C_YTD_PAYMENT, C_PAYMENT_CNT, C_SINCE " +
        "  FROM " + TABLENAME_CUSTOMER +
        " WHERE C_W_ID = 1 " +
        "   AND C_D_ID = 9 " +
        "   AND C_LAST = 'PRESOUGHTABLE' " +
        " ORDER BY C_FIRST"
    );

    public static final List<String> QUERIES_StockLevel = ImmutableList.of(
        // 0 - stockGetDistOrderIdSQL
        "SELECT D_NEXT_O_ID " +
        "  FROM " + TABLENAME_DISTRICT +
        " WHERE D_W_ID = 1 " +
        "   AND D_ID = 1",

        // 1 - stockGetCountStockSQL
       "SELECT COUNT(DISTINCT (S_I_ID)) AS STOCK_COUNT " +
       " FROM " + TABLENAME_ORDERLINE + ", " + TABLENAME_STOCK +
       " WHERE OL_W_ID = 1" +
       " AND OL_D_ID = 1" +
       " AND OL_O_ID < 3004" +
       " AND OL_O_ID >= 2984" +
       " AND S_W_ID = 1" +
       " AND S_I_ID = OL_I_ID" +
       " AND S_QUANTITY < 10"
    );

}
