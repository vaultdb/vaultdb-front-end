package org.vaultdb.parser.tpch;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelWriter;
import org.apache.calcite.rel.externalize.RelJsonWriter;
import org.apache.calcite.rel.logical.LogicalValues;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.sql.SqlExplainFormat;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.RelBuilder;
import org.vaultdb.TpcHBaseTest;
import org.vaultdb.codegen.JSONGenerator;
import org.vaultdb.config.SystemConfiguration;
import org.vaultdb.plan.SecureRelRoot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class QueryPlanToJSONTest extends TpcHBaseTest {

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
        SecureRelRoot root = new SecureRelRoot(testName, sql, false);

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
        String logicalPlan = RelOptUtil.dumpPlan("", root.getRelRoot().rel, SqlExplainFormat.TEXT, SqlExplainLevel.ALL_ATTRIBUTES);
        System.out.println("Logical plan: " + root);

        Map<Integer, String> plan = JSONGenerator.exportQueryPlan(root.getPlanRoot().getSecureRelNode());
        logger.info("Parsed plan for " + testName + ":\n" + plan);


    }




}

