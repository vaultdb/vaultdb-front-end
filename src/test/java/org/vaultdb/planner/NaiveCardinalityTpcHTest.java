package org.vaultdb.planner;

import java.util.logging.Level;

import org.apache.calcite.plan.RelOptUtil;
import org.vaultdb.TpcHBaseTest;
import org.vaultdb.plan.SecureRelRoot;

// basically need a broader model for deciding whether to execute in the clear or obliviously:
// * sensitivity of attributes we compute on
// * partitioning of data - partitioned-alike = plaintext exec w/obliv padding
// * replication status of tables
// * exec mode of max child (e.g., run filter in the clear on private data w/obliv padding,
// subsequent distributed ops are oblivious + MPC)

// this is the quad chart from before  (local, distributed (MPC) ) x (oblivious, cleartext)

// need to push down joins that can be executed in the clear, either owing to permissions,
// replication, or partitioning

// query rewrite rules (in order of operations):
// 1) push down joins with replicated tables
// 2) push down joins on public attributes (run after joins in #1)
// 3) push down joins that are partitioned-alike (run after joins in #2)

public class NaiveCardinalityTpcHTest extends TpcHBaseTest {

  protected void setUp() throws Exception {

    super.setUp();
  }

  public void testQuery01() throws Exception {
    runTest(1, 6001215);
  }

  public void testQuery03() throws Exception {
    runTest(3, 1350273375000000000L);
  }

  public void testQuery05() throws Exception {
    long expected = 150000 * 1000 * 25 * 20000 * 600000 * 25 * 5 * 15000; // TODO: Rohith, please check this
    runTest(5, expected);
  }


  public void testQuery08() throws Exception {
    runTest(8, 150000);
  }

  public void testQuery09() throws Exception {
    runTest(9, 3750000);
  }

  public void testQuery18() throws Exception {
    runTest(18, 90000000000L);
  }


  void runTest(int queryNo, long  expectedCardinality) throws Exception {

    String sql = super.readSQL(queryNo);
    String testName = "q" + queryNo;

    logger.log(Level.INFO, "Parsing " + sql);
    SecureRelRoot secRoot = new SecureRelRoot(testName, sql, true);

    logger.log(Level.INFO, "Parsed " + RelOptUtil.toString(secRoot.getRelRoot().project()));

    String testTree = secRoot.toString();
    logger.log(Level.INFO, "Resolved tree to:\n " + testTree);

    long observedCardinality = secRoot.getPlanRoot().getCardinalityBound();
    assertEquals(expectedCardinality, observedCardinality);



    logger.info("Completed test for Q" + queryNo);
  }
}
