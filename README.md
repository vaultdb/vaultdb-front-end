# VaultDB Front End

This suite demos a parser for extracting a DAG of database operators from a SQL statement.   It regularizes the operator order to push down filters and projections.  In addition, it eagerly projects out columns as they are no longer needed from the query's intermediate results.  This outputs a JSON file for use in the back-end.


## Dependencies

We recommend using this with:
* Java 18
* Maven 4+
* PostgreSQL 12+

Maven manages the remaining dependencies.  To reproduce the experiments...


**TODO: Xiling post pg_dumps of the tpch_dbs we used.  AFAIK, we don't have scripts to automate converting from floats to ints**


Build this with:

```
mvn compile
```
## Parsing a SQL Query to Its Canonicalized Query Tree


To generate a JSON query execution plan, run:
```
mvn compile exec:java -Dexec.mainClass="org.vaultdb.ParseSqlToJson" -Dexec.args="<db name> <file with SQL query>  <path to write output file>"
```

For example, to prepare a query for the `tpch` database in PostgreSQL with the query stored in `conf/workload/tpch/queries/01.sql` writing the query tree to `conf/workload/tpch/plans/01.json`, run:

```
mvn compile exec:java -Dexec.mainClass="org.vaultdb.ParseSqlToJson" -Dexec.args="tpch   conf/workload/tpch/queries/01.sql  conf/workload/tpch/plans"
```
