package org.vaultdb.plan.operator;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.adapter.jdbc.JdbcTableScan;
import org.apache.calcite.util.Pair;
import org.vaultdb.db.schema.SystemCatalog;
import org.vaultdb.config.ExecutionMode;
import org.vaultdb.plan.SecureRelNode;
import org.vaultdb.type.SecureRelRecordType;

public class SeqScan extends Operator {
	
	String tableName;
	SystemCatalog catalog;

	public SeqScan(String name, SecureRelNode src, Operator ... children ) throws Exception {
		super(name, src, children);

		catalog = SystemCatalog.getInstance();
		
		JdbcTableScan scan = (JdbcTableScan) src.getPhysicalNode().baseRelNode.getRelNode();
		tableName = scan.getTable().getQualifiedName().get(0);

		executionMode = new ExecutionMode();
		executionMode.distributed = false;
		executionMode.oblivious = false;
		executionMode.replicated = catalog.isReplicated(tableName);
		
	}
	
	public SecureRelRecordType getInSchema() {
		return baseRelNode.getSchema();
	}
	
	@Override
	public SecureRelRecordType getSchema(boolean isSecureLeaf) {
		return baseRelNode.getSchema();
	}
	


	@Override
	public void inferExecutionMode() {
		return; // done in constructor
	}

	@Override
	public long getCardinalityBound() {
		Pair<Long, Long> obliviousCardinality = catalog.getTableCardinalities(tableName);
		if (catalog.isReplicated(tableName)){
			return obliviousCardinality.left;
		}
		return obliviousCardinality.left + obliviousCardinality.right;
	}
	


}
