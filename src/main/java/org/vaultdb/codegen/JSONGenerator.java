package org.vaultdb.codegen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import jakarta.json.*;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalValues;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlExplainFormat;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.RelBuilder;
import org.vaultdb.codegen.sql.SqlGenerator;
import org.vaultdb.config.SystemConfiguration;
import org.vaultdb.plan.SecureRelNode;
import org.vaultdb.util.FileUtilities;
import org.vaultdb.util.Utilities;
import org.vaultdb.codegen.SqlInputForJSON;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// treat this as a struct

public class JSONGenerator {



    // returns JSON of the MPC query tree
    // populates map with <Calcite OperatorID, SQL string > OR <operator ID, JSON> pairs
    public static String exportQueryPlan(SecureRelNode secureRelNode, String testName) throws Exception {
        Map<RelNode, RelNode> replacements = new HashMap<RelNode, RelNode>();
        Map<Integer, String> planNodes =  new HashMap<Integer, String>();
        replacements = exportQueryPlanHelper(secureRelNode, replacements, planNodes);
        RelNode localCopy = secureRelNode.getRelNode();

        for(Map.Entry<RelNode, RelNode> entry : replacements.entrySet()) {
            localCopy = RelOptUtil.replace(localCopy, entry.getKey(), entry.getValue());
        }


        // write it into one long output
        String sqlOutput = new String();
        // first SQL statements:
        for(Map.Entry<Integer, String> entry : planNodes.entrySet()) {
            sqlOutput += "-- " + entry.getKey()   + "\n" + entry.getValue() + "\n";

        }

        // now add the root node
        //Integer rootID = localCopy.getId();
        String rootJSON =  RelOptUtil.dumpPlan("", localCopy, SqlExplainFormat.JSON, SqlExplainLevel.DIGEST_ATTRIBUTES);
        String outFilename = Utilities.getVaultDBRoot() + "/src/test/java/org/vaultdb/test/plans/mpc-" + testName + ".json";

        FileUtilities.writeFile(outFilename, rootJSON);

        String sqlFile = Utilities.getVaultDBRoot() + "/src/test/java/org/vaultdb/test/plans/queries-" + testName + ".sql";
        FileUtilities.writeFile(sqlFile, sqlOutput);

        return sqlOutput + "[root=" + rootJSON+ "]";

    }


    public static String exportGenericQueryPlan(SecureRelNode secureRelNode, String testName, String dstPath) throws Exception {
        Map<RelNode, RelNode> replacements = new HashMap<RelNode, RelNode>();
        Map<Integer, String> planNodes =  new HashMap<Integer, String>();
        replacements = exportQueryPlanHelper(secureRelNode, replacements, planNodes);
        RelNode localCopy = secureRelNode.getRelNode();

        for(Map.Entry<RelNode, RelNode> entry : replacements.entrySet()) {
            localCopy = RelOptUtil.replace(localCopy, entry.getKey(), entry.getValue());
        }


        // write it into one long output
        String sqlOutput = new String();
        // first SQL statements:
        for(Map.Entry<Integer, String> entry : planNodes.entrySet()) {
            sqlOutput += "-- " + entry.getKey()   + "\n" + entry.getValue() + "\n";

        }

        // now add the root node
        //Integer rootID = localCopy.getId();
        String rootJSON =  RelOptUtil.dumpPlan("", localCopy, SqlExplainFormat.JSON, SqlExplainLevel.DIGEST_ATTRIBUTES);
        String outFilename = Utilities.getVaultDBRoot() + "/" + dstPath + "/mpc-" + testName + ".json";

        FileUtilities.writeFile(outFilename, rootJSON);

        String sqlFile = Utilities.getVaultDBRoot() + "/" + dstPath + "/queries-" + testName + ".sql";
        FileUtilities.writeFile(sqlFile, sqlOutput);

        return sqlOutput + "[root=" + rootJSON+ "]";

    }

    // traverse tree and identify minimum covering set that must be performed under MPC with current tree config.
    // create secure leaf with LogicalValues and "sql" tag for input
    public static String extractMPCMinimizedQueryPlan(SecureRelNode aRoot) throws Exception {
        Map<RelNode, RelNode> replacements = new HashMap<RelNode, RelNode>();
        Map<Integer, SqlInputForJSON> planNodes =  new HashMap<Integer, SqlInputForJSON>();
        replacements = extractSQLFromSecureLeafs(aRoot, replacements, planNodes);
        RelNode localCopy = aRoot.getRelNode();

        for(Map.Entry<RelNode, RelNode> entry : replacements.entrySet()) {
            localCopy = RelOptUtil.replace(localCopy, entry.getKey(), entry.getValue());
        }

        String rootJSON =  RelOptUtil.dumpPlan("", localCopy, SqlExplainFormat.JSON, SqlExplainLevel.DIGEST_ATTRIBUTES);

        // TODO: parse json and insert "sql", collation, and PK-FK relationships
        JsonReader reader = Json.createReader(new StringReader(rootJSON));
        JsonObject ops = reader.readObject();

        JsonArray rels = ops.getJsonArray("rels");
        JsonArrayBuilder dst_rel_builder = Json.createArrayBuilder();

        for(int i = 0; i < rels.size(); i++) {

            JsonObject relNode = rels.getJsonObject(i);
            int id = Integer.parseInt(relNode.getString("id"));

            if(planNodes.containsKey(id)) {
                String sql = planNodes.get(id).getSql();
                sql = sql.replaceAll("\\n", " ");

                JsonValue sqlVal = Json.createValue(sql);

                JsonObjectBuilder builder = Json.createObjectBuilder();

                // iterate over relNode and add to builder
                for( Iterator<String> keys = relNode.keySet().iterator(); keys.hasNext(); ) {
                    String key = keys.next();
                    builder.add(key, relNode.get(key));
                }

                builder.add("sql", sqlVal);
                JsonObject newRelNode = builder.build();
                dst_rel_builder.add(newRelNode);
            }
            else {
                dst_rel_builder.add(relNode);
            }
        }
        JsonArray dst_array = dst_rel_builder.build();
        JsonObject dst = Json.createObjectBuilder().add("rels", dst_array).build();

        // pretty print it
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        Object jsonObject = mapper.readValue(dst.toString(), Object.class);
        return  mapper.writeValueAsString(jsonObject);
    }

    // disregard the access control (public/private) on each attribute.  Output entire query execution plan unconditionally
    // for use in ZKSQL
    public static String exportWholeQueryPlan(SecureRelNode aNode, String testName) throws Exception {
	 String rootJSON =  RelOptUtil.dumpPlan("", aNode.getRelNode(), SqlExplainFormat.JSON, SqlExplainLevel.DIGEST_ATTRIBUTES);
	 String outFilename = Utilities.getVaultDBRoot() + "/src/test/java/org/vaultdb/test/plans/whole/zk-" + testName + ".json";

     FileUtilities.writeFile(outFilename, rootJSON);

	 return rootJSON;

    }
    
    // replace plaintext subtrees with their corresponding SQL statements
    // return map of before and after for secure leafs
    static Map<RelNode, RelNode> exportQueryPlanHelper(SecureRelNode relNode, Map<RelNode, RelNode> replacements, Map<Integer, String> sqlNodes) throws Exception {
        for(SecureRelNode child : relNode.getChildren()) {
            // if a local plan, replace with SQL and terminate, otherwise recurse
            if(!child.getPhysicalNode().getExecutionMode().distributed) {
                Integer idx = sqlNodes.size();
                // Idx 1 + (idx  - 1) * 2 - may  need to frame this as a stack of inputs to get the right one as needed.
                Integer operatorNo = (idx < 2) ? idx : 1 + (idx - 1) * 2;

                RelNode childRelNode = child.getRelNode();
                List<RelCollation> sortOrder = childRelNode.getCollationList(); // TODO: fix this to use non-deprecated methods later
                System.out.println("Collation: " + sortOrder);

                String sql = SqlGenerator.getSourceSql(child.getPhysicalNode());
                sqlNodes.put(operatorNo, sql);

                // create an empty leaf
                FrameworkConfig calciteConfig =  SystemConfiguration.getInstance().getCalciteConfiguration();
                final RelBuilder builder = RelBuilder.create(calciteConfig);
                final RelNode leaf =  builder.values(child.getRelNode().getRowType()).build();
                replacements.put(child.getRelNode(), leaf);
            }
            else { // recurse
                replacements = exportQueryPlanHelper(child, replacements, sqlNodes);
            }
        }
        return replacements;
    }


    static Map<RelNode, RelNode> extractSQLFromSecureLeafs(SecureRelNode relNode, Map<RelNode, RelNode> replacements, Map<Integer, SqlInputForJSON> sqlNodes) throws Exception {
        for(SecureRelNode child : relNode.getChildren()) {
            // if a local plan, replace with SQL and terminate, otherwise recurse
            if(!child.getPhysicalNode().getExecutionMode().distributed) {
                Integer idx = sqlNodes.size();
                // Idx 1 + (idx  - 1) * 2 - may  need to frame this as a stack of inputs to get the right one as needed.
                Integer operatorNo = (idx < 2) ? idx : 1 + (idx - 1) * 2;

                RelNode childRelNode = child.getRelNode();
                List<RelCollation> sortOrder = childRelNode.getCollationList();

                String sql = SqlGenerator.getSourceSql(child.getPhysicalNode());
                RelCollation first = (sortOrder.size() > 0) ? sortOrder.get(0) : null;
                SqlInputForJSON sIn = new SqlInputForJSON(sql, first, operatorNo);
                sqlNodes.put(operatorNo, sIn);

                // create an empty leaf
                FrameworkConfig calciteConfig =  SystemConfiguration.getInstance().getCalciteConfiguration();
                final RelBuilder builder = RelBuilder.create(calciteConfig);
                final RelNode leaf =  builder.values(child.getRelNode().getRowType()).build();

                replacements.put(child.getRelNode(), leaf);
            }
            else { // return existing list
                replacements = extractSQLFromSecureLeafs(child, replacements, sqlNodes);
            }
        }
        return replacements;
    }



}
