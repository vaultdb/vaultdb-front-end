package org.vaultdb.planner;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.SqlExplainFormat;
import org.apache.calcite.sql.SqlExplainLevel;
import org.vaultdb.TpcHBaseTest;
import org.vaultdb.codegen.JSONGenerator;
import org.vaultdb.config.SystemConfiguration;
import org.vaultdb.plan.SecureRelRoot;
import org.vaultdb.util.FileUtilities;
import org.vaultdb.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.apache.calcite.util.Pair;


public class QueryPlanToJSONMPCMinimizationTest extends TpcHBaseTest {

    // FK --> PK
    // only works when the columns have not been renamed
    public static  ArrayList<Map<String,String> > integrityConstraints;

//    public static final Vector<Vector<Pair<String, String> > >  QUERIES = ImmutableList.of(


    protected void setUp() throws Exception {
        super.setUp();
        String dstDir = Utilities.getVaultDBRoot() + "/src/test/java/org/vaultdb/test/plans/mpc_min";
        Files.createDirectories(Paths.get(dstDir));


        // hardcode TPC-H integrity constraints
        integrityConstraints = new ArrayList<Map<String, String> >();

        Map<String, String>  fk = new HashMap<>();
        fk.put("n_regionkey", "r_regionkey");
        integrityConstraints.add(fk);

        fk.clear();
        fk.put("c_nationkey", "n_nationkey");
        integrityConstraints.add(fk);

        fk.clear();
        fk.put("s_nationkey", "n_nationkey");
        integrityConstraints.add(fk);

        fk.clear();
        fk.put("o_custkey", "c_custkey");
        integrityConstraints.add(fk);

        fk.clear();
        fk.put("l_orderkey", "o_orderkey");
        integrityConstraints.add(fk);

        fk.clear();
        fk.put("l_partkey", "p_partkey");
        integrityConstraints.add(fk);

        fk.clear();
        fk.put("l_suppkey", "s_suppkey");
        integrityConstraints.add(fk);

        fk.clear();
        fk.put("ps_partkey", "p_partkey");
        integrityConstraints.add(fk);

        fk.clear();
        fk.put("ps_suppkey", "s_suppkey");
        integrityConstraints.add(fk);

        fk.clear();
        fk.put("l_partkey", "ps_partkey");
        fk.put("l_suppkey", "ps_suppkey");
        integrityConstraints.add(fk);

    }

    public void testQuery01() throws Exception {
        String sql = QUERIES.get(0);
        String testName = "q" + 1;
        testCase(testName, sql);
    }


    public void testQuery02() throws Exception {
        String sql = QUERIES.get(1);
        String testName = "q" + 2;
        testCase(testName, sql);
    }

    public void testQuery03() throws Exception {
        String sql = QUERIES.get(2);
        String testName = "q" + 3;
        testCase(testName, sql);
    }

    public void testQuery04() throws Exception {
        String sql = QUERIES.get(3);
        String testName = "q" + 4;
        testCase(testName, sql);
    }

    public void testQuery05() throws Exception {
        String sql = QUERIES.get(4);
        String testName = "q" + 5;
        testCase(testName, sql);
    }

    public void testQuery06() throws Exception {
        String sql = QUERIES.get(5);
        String testName = "q" + 6;
        testCase(testName, sql);
    }

    public void testQuery07() throws Exception {
        String sql = QUERIES.get(6);
        String testName = "q" + 7;
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

    public void testQuery10() throws Exception {
        String sql = QUERIES.get(9);
        String testName = "q" + 10;
        testCase(testName, sql);
    }

    public void testQuery11() throws Exception {
        String sql = QUERIES.get(10);
        String testName = "q" + 11;
        testCase(testName, sql);
    }

    public void testQuery12() throws Exception {
        String sql = QUERIES.get(11);
        String testName = "q" + 12;
        testCase(testName, sql);
    }

    public void testQuery13() throws Exception {
        String sql = QUERIES.get(12);
        String testName = "q" + 13;
        testCase(testName, sql);
    }

    public void testQuery14() throws Exception {
        String sql = QUERIES.get(13);
        String testName = "q" + 14;
        testCase(testName, sql);
    }

    public void testQuery15() throws Exception {
        String sql = QUERIES.get(14);
        String testName = "q" + 15;
        testCase(testName, sql);
    }

    public void testQuery16() throws Exception {
        String sql = QUERIES.get(15);
        String testName = "q" + 16;
        testCase(testName, sql);
    }

    public void testQuery17() throws Exception {
        String sql = QUERIES.get(16);
        String testName = "q" + 17;
        testCase(testName, sql);
    }

    public void testQuery18() throws Exception {
        String sql = QUERIES.get(17);
        String testName = "q" + 18;
        testCase(testName, sql);
    }

    public void testQuery19() throws Exception {
        String sql = QUERIES.get(18);
        String testName = "q" + 19;
        testCase(testName, sql);
    }

    public void testQuery20() throws Exception {
        String sql = QUERIES.get(19);
        String testName = "q" + 20;
        testCase(testName, sql);
    }

    public void testQuery21() throws Exception {
        String sql = QUERIES.get(20);
        String testName = "q" + 21;
        testCase(testName, sql);
    }

    public void testQuery22() throws Exception {
        String sql = QUERIES.get(21);
        String testName = "q" + 22;
        testCase(testName, sql);
    }




    // test the lifecycle of parsing, creating DAG, and regenerating SQL
    // does it produce the same results after these transformations?
    protected void testCase(String testName, String sql) throws Exception {
        SystemConfiguration.getInstance().resetCounters();

        System.out.println("Parsing " + sql);
        SecureRelRoot root = new SecureRelRoot(testName, sql);

        System.out.println("Logical plan: " + root);

        String plan = JSONGenerator.extractMPCMinimizedQueryPlan(root.getPlanRoot().getSecureRelNode(), integrityConstraints);

        String dstDir = Utilities.getVaultDBRoot() + "/src/test/java/org/vaultdb/test/plans/mpc_min/";

        String outFilename = dstDir + "mpc-" + testName + ".json";

        FileUtilities.writeFile(outFilename, plan);




    }




}

