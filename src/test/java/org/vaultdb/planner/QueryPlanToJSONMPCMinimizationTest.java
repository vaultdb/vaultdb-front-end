package org.vaultdb.planner;

import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlExplainFormat;
import org.apache.calcite.sql.SqlExplainLevel;
import org.vaultdb.TpcHBaseTest;
import org.vaultdb.codegen.JSONGenerator;
import org.vaultdb.config.SystemConfiguration;
import org.vaultdb.plan.SecureRelRoot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class QueryPlanToJSONMPCMinimizationTest extends TpcHBaseTest {

    Map<String, ArrayList<RelNode>> operatorHistogram;
    Map<String, Integer> globalOperatorCounts;



    protected void setUp() throws Exception {
        super.setUp();
        globalOperatorCounts = new HashMap<>();
        operatorHistogram = new HashMap<>();
    }

    public void testQuery01() throws Exception {
        String sql = QUERIES.get(0);
        String testName = "q" + 1;
        testCase(testName, sql);
    }



    public void testQuery03() throws Exception {
        String sql = QUERIES.get(2);
        String testName = "q" + 3;
        testCase(testName, sql);
    }


    public void testQuery05() throws Exception {
        String sql = QUERIES.get(4);
        String testName = "q" + 5;
        testCase(testName, sql);
    }

    public void testQuery08() throws Exception {
        String sql = QUERIES.get(7);
        String testName = "q" + 8;
        testCase(testName, sql);
    }

    public void testQuery09() throws Exception {
        String sql = QUERIES.get(8);
        String testName = "q" + 9;
        testCase(testName, sql);
    }
    public void testQuery18() throws Exception {
        String sql = QUERIES.get(17);
        String testName = "q" + 18;
        testCase(testName, sql);
    }





    // test the lifecycle of parsing, creating DAG, and regenerating SQL
    // does it produce the same results after these transformations?
    protected void testCase(String testName, String sql) throws Exception {
        SystemConfiguration.getInstance().resetCounters();

        System.out.println("Parsing " + sql);
        SecureRelRoot root = new SecureRelRoot(testName, sql);

        System.out.println("Logical plan: " + root);

        String plan = JSONGenerator.extractMPCMinimizedQueryPlan(root.getPlanRoot().getSecureRelNode());
//        String plan = JSONGenerator.exportQueryPlan(root.getPlanRoot().getSecureRelNode(), testName);

        logger.info("Parsed plan for " + testName + ":\n" + plan);



    }




}

