package org.vaultdb.codegen;

import org.apache.calcite.rel.RelCollation;

public class SqlInputForJSON {
    String sql_;
    RelCollation collation_;
    int operatorId;

   public SqlInputForJSON(String s, RelCollation sort, int oid) {
       sql_ = s;
       collation_ = sort;
       operatorId = oid;
   }

    public String getSql() {
         return sql_;
    }

};