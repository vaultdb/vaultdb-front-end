package org.vaultdb.codegen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.json.*;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.*;
import org.apache.calcite.sql.SqlExplainFormat;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.RelBuilder;
import org.vaultdb.codegen.sql.SqlGenerator;
import org.vaultdb.config.SystemConfiguration;
import org.vaultdb.plan.SecureRelNode;
import org.vaultdb.util.FileUtilities;
import org.vaultdb.util.Utilities;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

// treat this as a struct

public class JSONGenerator {


    // returns JSON of the MPC query tree
    // populates map with <Calcite OperatorID, SQL string > OR <operator ID, JSON> pairs
    public static String exportQueryPlan(SecureRelNode secureRelNode, String testName) throws Exception {
        Map<RelNode, RelNode> replacements = new HashMap<RelNode, RelNode>();
        Map<Integer, String> planNodes = new HashMap<Integer, String>();
        replacements = exportQueryPlanHelper(secureRelNode, replacements, planNodes);
        RelNode localCopy = secureRelNode.getRelNode();

        for (Map.Entry<RelNode, RelNode> entry : replacements.entrySet()) {
            localCopy = RelOptUtil.replace(localCopy, entry.getKey(), entry.getValue());
        }


        // write it into one long output
        String sqlOutput = new String();
        // first SQL statements:
        for (Map.Entry<Integer, String> entry : planNodes.entrySet()) {
            sqlOutput += "-- " + entry.getKey() + "\n" + entry.getValue() + "\n";

        }

        // now add the root node
        //Integer rootID = localCopy.getId();
        String rootJSON = RelOptUtil.dumpPlan("", localCopy, SqlExplainFormat.JSON, SqlExplainLevel.DIGEST_ATTRIBUTES);
        String outFilename = Utilities.getVaultDBRoot() + "/src/test/java/org/vaultdb/test/plans/mpc-" + testName + ".json";

        FileUtilities.writeFile(outFilename, rootJSON);

        String sqlFile = Utilities.getVaultDBRoot() + "/src/test/java/org/vaultdb/test/plans/queries-" + testName + ".sql";
        FileUtilities.writeFile(sqlFile, sqlOutput);

        return sqlOutput + "[root=" + rootJSON + "]";

    }


    public static String exportGenericQueryPlan(SecureRelNode secureRelNode, String testName, String dstPath) throws Exception {
        Map<RelNode, RelNode> replacements = new HashMap<RelNode, RelNode>();
        Map<Integer, String> planNodes = new HashMap<Integer, String>();
        replacements = exportQueryPlanHelper(secureRelNode, replacements, planNodes);
        RelNode localCopy = secureRelNode.getRelNode();

        for (Map.Entry<RelNode, RelNode> entry : replacements.entrySet()) {
            localCopy = RelOptUtil.replace(localCopy, entry.getKey(), entry.getValue());
        }


        // write it into one long output
        String sqlOutput = new String();
        // first SQL statements:
        for (Map.Entry<Integer, String> entry : planNodes.entrySet()) {
            sqlOutput += "-- " + entry.getKey() + "\n" + entry.getValue() + "\n";

        }

        // now add the root node
        //Integer rootID = localCopy.getId();
        String rootJSON = RelOptUtil.dumpPlan("", localCopy, SqlExplainFormat.JSON, SqlExplainLevel.DIGEST_ATTRIBUTES);
        String outFilename = Utilities.getVaultDBRoot() + "/" + dstPath + "/mpc-" + testName + ".json";

        FileUtilities.writeFile(outFilename, rootJSON);

        String sqlFile = Utilities.getVaultDBRoot() + "/" + dstPath + "/queries-" + testName + ".sql";
        FileUtilities.writeFile(sqlFile, sqlOutput);

        return sqlOutput + "[root=" + rootJSON + "]";

    }

    // traverse tree and identify minimum covering set that must be performed under MPC with current tree config.
    // create secure leaf with LogicalValues and "sql" tag for input
    public static String extractMPCMinimizedQueryPlan(SecureRelNode aRoot, ArrayList<Map<String, String>> integrityConstraints) throws Exception {
        Map<RelNode, RelNode> replacements = new HashMap<RelNode, RelNode>();
        Map<Integer, SqlInputForJSON> planNodes = new HashMap<Integer, SqlInputForJSON>();
        replacements = extractSQLFromSecureLeafs(aRoot, replacements, planNodes);
        RelNode localCopy = aRoot.getRelNode();

        for (Map.Entry<RelNode, RelNode> entry : replacements.entrySet()) {
            localCopy = RelOptUtil.replace(localCopy, entry.getKey(), entry.getValue());
        }

        String rootJSON = RelOptUtil.dumpPlan("", localCopy, SqlExplainFormat.JSON, SqlExplainLevel.DIGEST_ATTRIBUTES);

        // first pass: add "sql" tag to each leaf node
        JsonObject withSql = addSqlTagToLeafs(rootJSON, planNodes);
        System.out.println("Relnode tree: ");
        System.out.println(RelOptUtil.dumpPlan("", localCopy, SqlExplainFormat.TEXT, SqlExplainLevel.ALL_ATTRIBUTES));
        JsonObject dst = addPKFKRelationships(withSql, localCopy, integrityConstraints);

        // pretty print it
        return prettyPrintJson(dst);
    }


    // returns "rels" array
    static JsonObject addSqlTagToLeafs(String rootJSON, Map<Integer, SqlInputForJSON> planNodes) {
        JsonReader reader = Json.createReader(new StringReader(rootJSON));
        JsonObject ops = reader.readObject();

        JsonArray rels = ops.getJsonArray("rels");
        JsonArrayBuilder dst_rel_builder = Json.createArrayBuilder();

        for (int i = 0; i < rels.size(); i++) {

            JsonObject relNode = rels.getJsonObject(i);
            int id = Integer.parseInt(relNode.getString("id"));

            if (planNodes.containsKey(id)) {
                String sql = planNodes.get(id).getSql();
                sql = sql.replaceAll("\\n", " ");
                JsonValue sqlVal = Json.createValue(sql);
                JsonObjectBuilder builder = Json.createObjectBuilder();

                // iterate over relNode and add to builder
                for (Iterator<String> keys = relNode.keySet().iterator(); keys.hasNext(); ) {
                    String key = keys.next();
                    builder.add(key, relNode.get(key));
                }

                builder.add("sql", sqlVal);
                JsonObject newRelNode = builder.build();
                dst_rel_builder.add(newRelNode);
            } else {
                dst_rel_builder.add(relNode);
            }
        }
        JsonArray dst_array = dst_rel_builder.build();
        return Json.createObjectBuilder().add("rels", dst_array).build();
    }

    // Q5:
    // LogicalSort(sort0=[$1], dir0=[DESC]): rowcount = 1.0, cumulative cost = 14.137500047683716, id = 221
    //  LogicalAggregate(group=[{0}], revenue=[SUM($1)]): rowcount = 1.0, cumulative cost = 13.137500047683716, id = 220
    //    LogicalProject(n_name=[$11], $f1=[*($6, -(1, $7))]): rowcount = 1.0, cumulative cost = 12.0, id = 219
    //      LogicalJoin(condition=[=($12, $13)], joinType=[inner]): rowcount = 1.0, cumulative cost = 11.0, id = 218
    //        LogicalJoin(condition=[=($9, $10)], joinType=[inner]): rowcount = 1.0, cumulative cost = 9.0, id = 213
    //          LogicalJoin(condition=[AND(=($5, $8), =($1, $9))], joinType=[inner]): rowcount = 1.0, cumulative cost = 7.0, id = 207
    //            LogicalJoin(condition=[=($4, $2)], joinType=[inner]): rowcount = 1.0, cumulative cost = 5.0, id = 206
    //              LogicalJoin(condition=[=($0, $3)], joinType=[inner]): rowcount = 1.0, cumulative cost = 3.0, id = 198
    //                LogicalValues(tuples=[[]]): rowcount = 1.0, cumulative cost = {1.0 rows, 1.0 cpu, 0.0 io}, id = 178
    //                LogicalValues(tuples=[[]]): rowcount = 1.0, cumulative cost = {1.0 rows, 1.0 cpu, 0.0 io}, id = 179
    //              LogicalValues(tuples=[[]]): rowcount = 1.0, cumulative cost = {1.0 rows, 1.0 cpu, 0.0 io}, id = 180
    //            LogicalValues(tuples=[[]]): rowcount = 1.0, cumulative cost = {1.0 rows, 1.0 cpu, 0.0 io}, id = 181
    //          LogicalValues(tuples=[[]]): rowcount = 1.0, cumulative cost = {1.0 rows, 1.0 cpu, 0.0 io}, id = 182
    //        LogicalValues(tuples=[[]]): rowcount = 1.0, cumulative cost = {1.0 rows, 1.0 cpu, 0.0 io}, id = 183

    static JsonObject addPKFKRelationships(JsonObject src, RelNode root, ArrayList<Map<String, String>> integrityConstraints) throws Exception {

        // for joins, add "fields" tag for output schema
        Map<Integer, RelNode> relNodes = new HashMap<>();
        extractRelNodes(root, relNodes);
        Map<Integer, RelNode> aligned = remapRelNodesToJsonIds(relNodes, src.getJsonArray("rels"));

        System.out.println("addPKFKRelationships  starting with " + prettyPrintJson(src));

        JsonArray rels = src.getJsonArray("rels");
        JsonArrayBuilder dstRelBuilder = Json.createArrayBuilder();

        for (int i = 0; i < rels.size(); i++) {

            JsonObject node = rels.getJsonObject(i);
            int id = Integer.parseInt(node.getString("id"));
            RelNode relNode = aligned.get(id);

            if (relNode instanceof LogicalJoin) {
                // check for PK-FK relationships
                LogicalJoin join = (LogicalJoin) relNode;
                RexNode predicate = join.getCondition();
                RexBuilder rexBuilder = join.getCluster().getRexBuilder();
                RexNode joinOn = RexUtil.toCnf(rexBuilder, predicate);
                Map<String, String> joinKeys = new HashMap<>();
                extractJoinKeys(joinOn, join.getRowType(), joinKeys);


                // check if joinKeys are in integrityConstraints
                int fkRelation = getForeignKeyRelationship(joinKeys, integrityConstraints);
                if (fkRelation != -1) {
                    // add to JSON
                    JsonObjectBuilder builder = Json.createObjectBuilder();
                    for (Iterator<String> keys = node.keySet().iterator(); keys.hasNext(); ) {
                        String key = keys.next();
                        builder.add(key, node.get(key));
                    }
                    JsonValue fkValue = Json.createValue(fkRelation);
                    builder.add("foreignKey", fkValue);
                    JsonObject newRelNode = builder.build();
                    dstRelBuilder.add(newRelNode);
                }
            } else {
                dstRelBuilder.add(node);
            }

        } // end for-loop for all rels


        JsonArray dstArray = dstRelBuilder.build();
        JsonObject j = Json.createObjectBuilder().add("rels", dstArray).build();
        System.out.println("addPKFKRelationships  ending with " +  prettyPrintJson(j));

        return j;

    }


    public JSONGenerator() {
    }

    static Map<Integer, RelNode> remapRelNodesToJsonIds(Map<Integer, RelNode> relNodes, JsonArray jsonOps) {
        Map<Integer, RelNode> dst = new HashMap<>();
        int operatorIdCounter = 0;

        assert (relNodes.size() <= jsonOps.size() - 1);
        List<Integer> sortedKeys = relNodes.keySet().stream().sorted().collect(Collectors.toList());
        Iterator<Integer> keyPos = sortedKeys.iterator();

        // map to BFS order
        // Planner first serializes leafs, and seems to place remaining ops in BFS order
        // occasionally it throws an extra projection on top.


        Map<Integer, RelNode> leafs = new HashMap<>();
        Map<Integer, RelNode> internalNodes = new HashMap<>();

        // serialize LogicalValues first
        for (int i = 0; i < jsonOps.size(); i++) {
            JsonObject node = jsonOps.getJsonObject(i);
            if (node.getString("relOp").equals("LogicalValues")) {
                Integer oldKey = keyPos.next();
                dst.put(i, relNodes.get(oldKey));
                System.out.println(oldKey + " -> " + i);
//                ++operatorIdCounter;
            }

        }


        // ok, now cover the remaining ones
        // sorted key order forces us to do internal nodes after all leafs
        for (int i = 0; i < jsonOps.size(); i++) {
            if (!keyPos.hasNext()) break;

            JsonObject node = jsonOps.getJsonObject(i);

            if (!node.getString("relOp").equals("LogicalValues")) {
                Integer oldKey = keyPos.next();
                dst.put(i, relNodes.get(oldKey));
                System.out.println(oldKey + " -> " + i);
//                ++operatorIdCounter;
            }
        }


        return dst;
    }

    // disregard the access control (public/private) on each attribute.  Output entire query execution plan unconditionally
    // for use in ZKSQL
    public static String exportWholeQueryPlan(SecureRelNode aNode, String testName) throws Exception {
        String rootJSON = RelOptUtil.dumpPlan("", aNode.getRelNode(), SqlExplainFormat.JSON, SqlExplainLevel.DIGEST_ATTRIBUTES);
        String outFilename = Utilities.getVaultDBRoot() + "/src/test/java/org/vaultdb/test/plans/whole/zk-" + testName + ".json";

        FileUtilities.writeFile(outFilename, rootJSON);

        return rootJSON;

    }

    // replace plaintext subtrees with their corresponding SQL statements
    // return map of before and after for secure leafs
    static Map<RelNode, RelNode> exportQueryPlanHelper(SecureRelNode relNode, Map<RelNode, RelNode> replacements, Map<Integer, String> sqlNodes) throws Exception {
        for (SecureRelNode child : relNode.getChildren()) {
            // if a local plan, replace with SQL and terminate, otherwise recurse
            if (!child.getPhysicalNode().getExecutionMode().distributed) {
                Integer idx = sqlNodes.size();
                // Idx 1 + (idx  - 1) * 2 - may  need to frame this as a stack of inputs to get the right one as needed.
                Integer operatorNo = (idx < 2) ? idx : 1 + (idx - 1) * 2;

                RelNode childRelNode = child.getRelNode();
                List<RelCollation> sortOrder = childRelNode.getCollationList(); // TODO: fix this to use non-deprecated methods later
                System.out.println("Collation: " + sortOrder);

                String sql = SqlGenerator.getSourceSql(child.getPhysicalNode());
                sqlNodes.put(operatorNo, sql);

                // create an empty leaf
                FrameworkConfig calciteConfig = SystemConfiguration.getInstance().getCalciteConfiguration();
                final RelBuilder builder = RelBuilder.create(calciteConfig);
                final RelNode leaf = builder.values(child.getRelNode().getRowType()).build();
                replacements.put(child.getRelNode(), leaf);
            } else { // recurse
                replacements = exportQueryPlanHelper(child, replacements, sqlNodes);
            }
        }
        return replacements;
    }


    static Map<RelNode, RelNode> extractSQLFromSecureLeafs(SecureRelNode relNode, Map<RelNode, RelNode> replacements, Map<Integer, SqlInputForJSON> sqlNodes) throws Exception {
        for (SecureRelNode child : relNode.getChildren()) {
            // if a local plan, replace with SQL and terminate, otherwise recurse
            if (!child.getPhysicalNode().getExecutionMode().distributed) {
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
                FrameworkConfig calciteConfig = SystemConfiguration.getInstance().getCalciteConfiguration();
                final RelBuilder builder = RelBuilder.create(calciteConfig);
                final RelNode leaf = builder.values(child.getRelNode().getRowType()).build();

                replacements.put(child.getRelNode(), leaf);
            } else { // return existing list
                replacements = extractSQLFromSecureLeafs(child, replacements, sqlNodes);
            }
        }
        return replacements;
    }

    // return a map of all RelNodes
    // use this to deduce the PK-FK joins
    static void extractRelNodes(RelNode relNode, Map<Integer, RelNode> nodes) throws Exception {
        for (RelNode child : relNode.getInputs()) {
            nodes.put(child.getId(), child);
            extractRelNodes(child, nodes);
        }

    }

    // only supports conjunctive predicates
    static void extractJoinKeys(RexNode joinOn, RelDataType schema, Map<String, String> keys) throws Exception {
        if (joinOn.getKind() == SqlKind.AND) {
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
                if (lOrdinal > rOrdinal) {
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
    static int getForeignKeyRelationship(Map<String, String> joinKeys, ArrayList<Map<String, String>> integrityConstraints) {
        int fk = -1;
        for (Map<String, String> ic : integrityConstraints) {
            // see if it matches the entirety of ic.  It's ok if this is only a subset of joinKey, e.g., s_nationkey = c_nationkey in Q5 of TPC-H
            for (Map.Entry<String, String> entry : ic.entrySet()) {
                // if joinKey lhs = FK, joinKey rhs = PK
                if (joinKeys.containsKey(entry.getKey()) && joinKeys.get(entry.getKey()).equals(entry.getValue())) {
                    if (fk == -1) {
                        fk = 0;
                    } else if (fk == 1) {
                        fk = -1;  // try again with a different IC
                        break;
                    }
                }

                // joinKey lhs = PK, joinKey rhs = FK
                else if (joinKeys.containsKey(entry.getValue()) && joinKeys.get(entry.getValue()).equals(entry.getKey())) {
                    if (fk == -1) {
                        fk = 1;
                    } else if (fk == 1) {
                        fk = -1;
                        break;
                    }
                }

            } // end for loop on this IC
            // if we found a match, return this one greedily
           if(fk != -1) return fk;
        }
        return fk;
    }


    static String prettyPrintJson(JsonObject j) throws Exception {
        // pretty print it
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        Object obj = mapper.readValue(j.toString(), Object.class);
        return  mapper.writeValueAsString(obj);
    }

}
