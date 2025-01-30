package org.vaultdb.codegen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import jakarta.json.*;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalValues;
import org.apache.calcite.rel.rel2sql.RelToSqlConverter;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.*;
import org.apache.calcite.sql.SqlExplainFormat;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vaultdb.codegen.sql.SqlGenerator;
import org.vaultdb.config.SystemConfiguration;
import org.vaultdb.plan.SecureRelNode;
import org.vaultdb.type.SecureRelDataTypeField;
import org.vaultdb.type.SecureRelRecordType;
import org.vaultdb.util.FileUtilities;
import org.vaultdb.util.Utilities;
import org.vaultdb.codegen.SqlInputForJSON;

import java.io.StringReader;
import java.util.*;

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
    public static String extractMPCMinimizedQueryPlan(SecureRelNode aRoot, ArrayList<Map<String,String> > integrityConstraints) throws Exception {
        Map<RelNode, RelNode> replacements = new HashMap<RelNode, RelNode>();
        Map<Integer, SqlInputForJSON> planNodes =  new HashMap<Integer, SqlInputForJSON>();
        replacements = extractSQLFromSecureLeafs(aRoot, replacements, planNodes);
        RelNode localCopy = aRoot.getRelNode();

        for(Map.Entry<RelNode, RelNode> entry : replacements.entrySet()) {
            localCopy = RelOptUtil.replace(localCopy, entry.getKey(), entry.getValue());
        }

        String rootJSON =  RelOptUtil.dumpPlan("", localCopy, SqlExplainFormat.JSON, SqlExplainLevel.DIGEST_ATTRIBUTES);

        // TODO: parse json and insert collation
        JsonReader reader = Json.createReader(new StringReader(rootJSON));
        JsonObject ops = reader.readObject();

        // first pass: add "sql" tag to each leaf node
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

        // second pass: add "inputFields" and "outputFields" tag to all unary nodes
        // for joins, add "fields" tag for output schema
        Map<Integer, RelNode> relNodes = new HashMap<>();
        extractRelNodes(localCopy, relNodes);

        // start with leaf nodes, derive schema from corresponding RelNode
        // leafs are LogicalValues and appear to be numbered from DFS traversal
        // Example plan:
        // LogicalSort#145
        //-> LogicalAggregate#144
        //   -> LogicalProject#143
        //      -> LogicalJoin#142
        //         -> LogicalJoin#136
        //      	    -> LogicalValues#127
        //      	    -> LogicalValues#128
        //      	 ->LogicalValues#129

        // TODO: JMR return here
        // start by modularizing this code 1 / pass


        // third pass: add PK-FK relationships

        JsonObject src = Json.createObjectBuilder().add("rels", dst_array).build();

         rels = src.getJsonArray("rels");
        JsonArrayBuilder dstRelBuilder = Json.createArrayBuilder();

        for(int i = 0; i < rels.size(); i++) {

            JsonObject node = rels.getJsonObject(i);
            int id = Integer.parseInt(node.getString("id"));
            RelNode relNode = relNodes.get(id);
            // JMR: need to embed name of field in JSON to map it to PK-FK relationships
            if(relNode instanceof LogicalJoin) {
                // check for PK-FK relationships
                LogicalJoin join = (LogicalJoin) relNode;
                RexNode predicate = join.getCondition();
                RexBuilder rexBuilder = join.getCluster().getRexBuilder();
                RexNode joinOn = RexUtil.toCnf(rexBuilder, predicate);
                Map<String, String> joinKeys = new HashMap<>();
                extractJoinKeys(joinOn, join.getRowType(), joinKeys);


                // check if joinKeys are in integrityConstraints
                int fkRelation = getForeignKeyRelation(joinKeys, integrityConstraints);
                if(fkRelation != -1) {
                    // add to JSON
                    JsonObjectBuilder builder = Json.createObjectBuilder();
                    for( Iterator<String> keys = node.keySet().iterator(); keys.hasNext(); ) {
                        String key = keys.next();
                        builder.add(key, node.get(key));
                    }

                    builder.add("foreignKey", fkRelation);
                    JsonObject newRelNode = builder.build();
                    dstRelBuilder.add(newRelNode);
                }
            }
            else {
                dstRelBuilder.add(node);
            }

        } // end for-loop for all rels


        JsonArray dstArray = dstRelBuilder.build();
        JsonObject dst = Json.createObjectBuilder().add("rels", dstArray).build();
            // pretty print it
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        Object obj = mapper.readValue(dst.toString(), Object.class);
        return  mapper.writeValueAsString(obj);
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

    // return a map of all RelNodes
    // use this to deduce the PK-FK joins
    static void extractRelNodes(RelNode relNode, Map<Integer, RelNode> nodes) throws Exception {
        for(RelNode child : relNode.getInputs()) {
            nodes.put(child.getId(), child);
            extractRelNodes(child, nodes);
        }

    }

    // only supports conjunctive predicates
    static void extractJoinKeys(RexNode joinOn, RelDataType schema,  Map<String, String> keys) throws Exception {
        if (joinOn.getKind() == SqlKind.AND) {  // TODO: handle > 2 selection criteria?
            List<RexNode> operands = new ArrayList<RexNode>(((RexCall) joinOn).operands);
            for (RexNode op : operands) {
                extractJoinKeys(op, schema, keys);
            }
        } else if (joinOn.getKind() == SqlKind.EQUALS) {
            RexCall cOp = (RexCall) joinOn;
            RexNode lhs = cOp.getOperands().get(0);
            RexNode rhs = cOp.getOperands().get(1);

            if (lhs.getKind() == SqlKind.INPUT_REF && rhs.getKind() == SqlKind.INPUT_REF) {
                RexInputRef lhsRef = (RexInputRef) lhs;
                RexInputRef rhsRef = (RexInputRef) rhs;

                int lOrdinal = lhsRef.getIndex();
                int rOrdinal = rhsRef.getIndex();

                // sort ordinals s.t. lhs comes first
                if(lOrdinal > rOrdinal) {
                    int tmp = lOrdinal;
                    lOrdinal = rOrdinal;
                    rOrdinal = tmp;
                }

                RelDataTypeField lField = schema.getFieldList().get(lOrdinal);
                String lFieldName = lField.getName();

                RelDataTypeField rField = schema.getFieldList().get(rOrdinal);
                String rFieldName = rField.getName();

                keys.put(lFieldName, rFieldName);

            } else {
                throw new Exception("Unsupported join predicate");
            }
        }
    }

    // returns 0 if lhs is FK, 1 if RHS is FK, -1 if neither
    // integrity constraints are (FK, PK) pairs
    static int getForeignKeyRelation(Map<String, String> joinKeys, ArrayList<Map<String, String> > integrityConstraints) {
        int fk = -1;
        for(Map<String, String> ic : integrityConstraints) {
           if(ic.size() == joinKeys.size()) {
               for (Map.Entry<String, String> entry : ic.entrySet()) {
                   if (joinKeys.containsKey(entry.getKey()) && joinKeys.get(entry.getKey()).equals(entry.getValue())) {
                       // if uninitialized, set to 0
                       if (fk == -1) {
                           fk = 0;
                       }
                       if (fk == 1) {
                           return -1;
                       } // not a match
                   } else if (joinKeys.containsKey(entry.getValue()) && joinKeys.get(entry.getValue()).equals(entry.getKey())) {
                       if (fk == -1) {
                           fk = 1;
                       }
                       if (fk == 0) {
                           return -1;
                       } // not a match
                   }
               } // end for loop for this IC
           } // end check that they have the same # of equalities
        } // end for loop for all ICs
        return fk;
    }


}
