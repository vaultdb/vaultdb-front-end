# VaultDB Front End

This suite demos a parser for extracting a DAG of database operators from a SQL statement.   It regularizes the operator order to push down filters and projections.  In addition, it eagerly projects out columns as they are no longer needed from the query's intermediate results.  This outputs a JSON file for use in the back-end.


## Dependencies

We recommend using this with:
* Java 18
* Maven 4+
* PostgreSQL 12+

Maven handles the remaining dependencies.

You can build this with:

```
mvn compile
```
