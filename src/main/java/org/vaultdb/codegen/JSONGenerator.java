package org.vaultdb.codegen;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalValues;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.sql.SqlExplainFormat;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.RelBuilder;
import org.vaultdb.codegen.sql.SqlGenerator;
import org.vaultdb.config.SystemConfiguration;
import org.vaultdb.plan.SecureRelNode;

import java.util.HashMap;
import java.util.Map;

public class JSONGenerator {
    // returns JSON of the MPC query tree
    // populates map with <Calcite OperatorID, SQL string > OR <operator ID, JSON> pairs
    public static Map<Integer, String> exportQueryPlan(SecureRelNode secureRelNode) throws Exception {
        Map<RelNode, RelNode> replacements = new HashMap<RelNode, RelNode>();
        Map<Integer, String> planNodes =  new HashMap<Integer, String>();
        replacements = exportQueryPlanHelper(secureRelNode, replacements, planNodes);
        RelNode localCopy = secureRelNode.getRelNode();

        for(Map.Entry<RelNode, RelNode> entry : replacements.entrySet()) {
            localCopy = RelOptUtil.replace(localCopy, entry.getKey(), entry.getValue());
        }

        Integer rootID = localCopy.getId();
        String rootJSON =  RelOptUtil.dumpPlan("", localCopy, SqlExplainFormat.JSON, SqlExplainLevel.DIGEST_ATTRIBUTES);
        planNodes.put(rootID, rootJSON);

        return planNodes;
    }

    // replace plaintext subtrees with their corresponding SQL statements
    // return map of before and after for secure leafs
    static Map<RelNode, RelNode> exportQueryPlanHelper(SecureRelNode relNode, Map<RelNode, RelNode> replacements, Map<Integer, String> sqlNodes) throws Exception {
        for(SecureRelNode child : relNode.getChildren()) {
            // if a local plan, replace with SQL and terminate, otherwise recurse
            if(!child.getPhysicalNode().getExecutionMode().distributed) {
                Integer operatorId = child.getRelNode().getId();
                String sql = SqlGenerator.getSourceSql(child.getPhysicalNode());
                sqlNodes.put(operatorId, sql);

                // create an empty leaf
                FrameworkConfig calciteConfig =  SystemConfiguration.getInstance().getCalciteConfiguration();
                final RelBuilder builder = RelBuilder.create(calciteConfig);

                final RelNode leaf =  builder.values(child.getRelNode().getRowType()).build();

                // values encoded in tuple object of JSON
                //builder
                //        .values(new String[] {"operator-id", "sql statement"}, operatorId, sql);
                //LogicalValues secureLeaf = (LogicalValues) builder.build();

                replacements.put(child.getRelNode(), leaf);
            }
            else { // return existing list
                replacements = exportQueryPlanHelper(child, replacements, sqlNodes);
            }
        }
        return replacements;
    }



    static String generateSql(RelNode node) {
        RelToSqlConverter converter = new RelToSqlConverter(SystemConfiguration.DIALECT);

        SqlSelect sql = converter.visitChild(0, node).asSelect();
        String sqlOut = sql.toSqlString(SystemConfiguration.DIALECT).getSql();
        sqlOut = sqlOut.replace("\"", "");
        return sqlOut;
    }
}
