 WITH customer_selection AS ( 
        SELECT substring(c_phone from 1 for 2) as cntrycode, c_acctbal, c_custkey 
        FROM customer 
        WHERE substring(c_phone from 1 for 2) in 
             ('24', '31', '11', '16', '21', '20', '34')), 
     c_avg_acctbal AS ( 
 	      SELECT avg(c_acctbal)  
 	      FROM customer_selection 
 	      WHERE c_acctbal > 0.0) 
 SELECT cntrycode, count(*) as numcust, sum(c_acctbal) as totacctbal 
 FROM customer_selection c LEFT OUTER JOIN orders o  ON o.o_custkey = c.c_custkey 
 WHERE c_acctbal > (SELECT * FROM c_avg_acctbal) AND o.o_custkey IS NULL 
 GROUP BY cntrycode 
 ORDER BY cntrycode 