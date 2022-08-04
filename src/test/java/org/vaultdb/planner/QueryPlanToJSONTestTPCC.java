package org.vaultdb.planner;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlExplainFormat;
import org.apache.calcite.sql.SqlExplainLevel;
import org.vaultdb.TpcCBaseTest;
import org.vaultdb.codegen.JSONGenerator;
import org.vaultdb.config.SystemConfiguration;
import org.vaultdb.plan.SecureRelRoot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class QueryPlanToJSONTestTPCC extends TpcCBaseTest {

    Map<String, ArrayList<RelNode>> operatorHistogram;
    Map<String, Integer> globalOperatorCounts;



    protected void setUp() throws Exception {
        super.setUp();
        globalOperatorCounts = new HashMap<>();
        operatorHistogram = new HashMap<>();
    }

    public void testDeliveryQuery00() throws Exception {
        String sql = QUERIES_Delivery.get(0);
        String testName = "delivery_q " + 0;
        testCase(testName, sql);
    }

    public void testDeliveryQuery01() throws Exception {
        String sql = QUERIES_Delivery.get(1);
        String testName = "delivery_q" + 1;
        testCase(testName, sql);
    }

    public void testDeliveryQuery02() throws Exception {
        String sql = QUERIES_Delivery.get(2);
        String testName = "delivery_q" + 2;
        testCase(testName, sql);
    }

    public void testDeliveryQuery03() throws Exception {
        String sql = QUERIES_Delivery.get(3);
        String testName = "delivery_q" + 3;
        testCase(testName, sql);
    }

    public void testDeliveryQuery04() throws Exception {
        String sql = QUERIES_Delivery.get(4);
        String testName = "delivery_q" + 4;
        testCase(testName, sql);
    }

    public void testDeliveryQuery05() throws Exception {
        String sql = QUERIES_Delivery.get(5);
        String testName = "delivery_q" + 5;
        testCase(testName, sql);
    }

    public void testDeliveryQuery06() throws Exception {
        String sql = QUERIES_Delivery.get(6);
        String testName = "delivery_q" + 6;
        testCase(testName, sql);
    }

    public void testNewOrderQuery00() throws Exception {
        String sql = QUERIES_NewOrder.get(0);
        String testName = "newOrder_q" + 0;
        testCase(testName, sql);
    }

    public void testNewOrderQuery01() throws Exception {
        String sql = QUERIES_NewOrder.get(1);
        String testName = "newOrder_q" + 1;
        testCase(testName, sql);
    }

    public void testNewOrderQuery02() throws Exception {
        String sql = QUERIES_NewOrder.get(2);
        String testName = "newOrder_q" + 2;
        testCase(testName, sql);
    }

    public void testNewOrderQuery03() throws Exception {
        String sql = QUERIES_NewOrder.get(3);
        String testName = "newOrder_q" + 3;
        testCase(testName, sql);
    }

    public void testNewOrderQuery04() throws Exception {
        String sql = QUERIES_NewOrder.get(4);
        String testName = "newOrder_q" + 4;
        testCase(testName, sql);
    }

    public void testNewOrderQuery05() throws Exception {
        String sql = QUERIES_NewOrder.get(5);
        String testName = "newOrder_q" + 5;
        testCase(testName, sql);
    }

    public void testNewOrderQuery06() throws Exception {
        String sql = QUERIES_NewOrder.get(6);
        String testName = "newOrder_q" + 6;
        testCase(testName, sql);
    }
    public void testNewOrderQuery07() throws Exception {
        String sql = QUERIES_NewOrder.get(7);
        String testName = "newOrder_q" + 7;
        testCase(testName, sql);
    }
    public void testNewOrderQuery08() throws Exception {
        String sql = QUERIES_NewOrder.get(8);
        String testName = "newOrder_q" + 8;
        testCase(testName, sql);
    }
    public void testNewOrderQuery09() throws Exception {
        String sql = QUERIES_NewOrder.get(9);
        String testName = "newOrder_q" + 9;
        testCase(testName, sql);
    }

    public void testOrderStatusQuery00() throws Exception {
        String sql = QUERIES_OrderStatus.get(0);
        String testName = "orderStatus_q" + 0;
        testCase(testName, sql);
    }

    public void testOrderStatusQuery01() throws Exception {
        String sql = QUERIES_OrderStatus.get(1);
        String testName = "orderStatus_q" + 1;
        testCase(testName, sql);
    }

    public void testOrderStatusQuery02() throws Exception {
        String sql = QUERIES_OrderStatus.get(2);
        String testName = "orderStatus_q" + 2;
        testCase(testName, sql);
    }

    public void testOrderStatusQuery03() throws Exception {
        String sql = QUERIES_OrderStatus.get(3);
        String testName = "orderStatus_q" + 3;
        testCase(testName, sql);
    }

    public void testPaymentQuery00() throws Exception {
        int query_number = 0;
        String sql = QUERIES_Payment.get(query_number);
        String testName = "payment_q" + query_number;
        testCase(testName, sql);
    }

    public void testPaymentQuery01() throws Exception {
        int query_number = 1;
        String sql = QUERIES_Payment.get(query_number);
        String testName = "payment_q" + query_number;
        testCase(testName, sql);
    }

    public void testPaymentQuery02() throws Exception {
        int query_number = 2;
        String sql = QUERIES_Payment.get(query_number);
        String testName = "payment_q" + query_number;
        testCase(testName, sql);
    }

    public void testPaymentQuery03() throws Exception {
        int query_number = 3;
        String sql = QUERIES_Payment.get(query_number);
        String testName = "payment_q" + query_number;
        testCase(testName, sql);
    }

    public void testPaymentQuery04() throws Exception {
        int query_number = 4;
        String sql = QUERIES_Payment.get(query_number);
        String testName = "payment_q" + query_number;
        testCase(testName, sql);
    }

    public void testPaymentQuery05() throws Exception {
        int query_number = 5;
        String sql = QUERIES_Payment.get(query_number);
        String testName = "payment_q" + query_number;
        testCase(testName, sql);
    }

    public void testPaymentQuery06() throws Exception {
        int query_number = 6;
        String sql = QUERIES_Payment.get(query_number);
        String testName = "payment_q" + query_number;
        testCase(testName, sql);
    }

    public void testPaymentQuery07() throws Exception {
        int query_number = 7;
        String sql = QUERIES_Payment.get(query_number);
        String testName = "payment_q" + query_number;
        testCase(testName, sql);
    }

    public void testPaymentQuery08() throws Exception {
        int query_number = 8;
        String sql = QUERIES_Payment.get(query_number);
        String testName = "payment_q" + query_number;
        testCase(testName, sql);
    }

    public void testStockLevelQuery00() throws Exception {
        int query_number = 0;
        String sql = QUERIES_StockLevel.get(query_number);
        String testName = "stockLevel_q" + query_number;
        testCase(testName, sql);
    }

    public void testStockLevelQuery01() throws Exception {
        int query_number = 1;
        String sql = QUERIES_StockLevel.get(query_number);
        String testName = "stockLevel_q" + query_number;
        testCase(testName, sql);
    }



    // test the lifecycle of parsing, creating DAG, and regenerating SQL
    // does it produce the same results after these transformations?
    protected void testCase(String testName, String sql) throws Exception {
        SystemConfiguration.getInstance().resetCounters();

        System.out.println("Parsing " + sql);
        SecureRelRoot root = new SecureRelRoot(testName, sql);


        String logicalPlan = RelOptUtil.dumpPlan("", root.getRelRoot().rel, SqlExplainFormat.TEXT, SqlExplainLevel.ALL_ATTRIBUTES);
        System.out.println("Logical plan: " + root);

        String plan = JSONGenerator.exportQueryPlan(root.getPlanRoot().getSecureRelNode(), testName);
        logger.info("Parsed plan for " + testName + ":\n" + plan);



    }

    protected void wholeTestCase(String testName, String sql) throws Exception {
        SecureRelRoot root = new SecureRelRoot(testName, sql);

        String logicalPlan = RelOptUtil.dumpPlan("", root.getRelRoot().rel, SqlExplainFormat.TEXT, SqlExplainLevel.ALL_ATTRIBUTES);
        System.out.println("Logical plan: " + root);

        String plan = JSONGenerator.exportWholeQueryPlan(root.getPlanRoot().getSecureRelNode(), testName);
        logger.info("Parsed plan for " + testName + ":\n" + plan);

    }


    /* scratch:
            // use this to write out the specs for secure nodes
        RelWriter planWriter = new RelJsonWriter();
        Map<Integer, String> sqlInputs = new HashMap<Integer, String>(); // map operator ID to a SQL statement

        // root.getRelRoot().rel.explain(planWriter);
        //return ((RelJsonWriter) planWriter).asString();
        RelNode rootNode = root.getRelRoot().rel;

        // replace each secure leaf with a dummy RelNode
        // use RelOptUtil.replaceInput()  or RelOptUtil.replace()
/*
       FrameworkConfig calciteConfig =  SystemConfiguration.getInstance().getCalciteConfiguration();
        final RelBuilder builder = RelBuilder.create(calciteConfig);

        // values encoded in tuple object of JSON
        builder
                .values(new String[] {"operator-id", "sql statement"}, 1, "SELECT * FROM ");
        LogicalValues tmp = (LogicalValues) builder.build();

        ImmutableList<ImmutableList<RexLiteral>> tuples = tmp.getTuples();
        Integer operatorId = tuples.get(0).get(0).getValueAs(Integer.class);
        String sqlStatement = tuples.get(0).get(1).getValueAs(String.class);



        System.out.println("Builder: " + operatorId + " --> " + sqlStatement);
        String leaf = RelOptUtil.dumpPlan("", tmp, SqlExplainFormat.JSON, SqlExplainLevel.DIGEST_ATTRIBUTES);
        System.out.println("Simulated secure leaf: " + leaf);


*/



}

