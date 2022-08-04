-- 0
SELECT *
FROM (SELECT n_nationkey, n_regionkey
FROM nation) AS t
INNER JOIN (SELECT r_regionkey
FROM (SELECT r_regionkey, r_name
FROM region) AS t0
WHERE r_name = 'MIDDLE EAST              ') AS t2 ON t.n_regionkey = t2.r_regionkey
INNER JOIN (SELECT c_custkey, c_nationkey
FROM customer) AS t3 ON t.n_nationkey = t3.c_nationkey
-- 1
SELECT o_orderkey, o_custkey, o_orderyear AS o_year, NOT (o_orderdate >= DATE '1995-01-01' AND o_orderdate <= DATE '1996-12-31') AS dummy_tag
FROM (SELECT o_orderkey, o_custkey, o_orderdate, o_orderyear
FROM orders) AS t
ORDER BY o_orderkey, o_custkey, o_orderdate, o_orderyear
-- 3
SELECT *
FROM (SELECT p_partkey
FROM (SELECT p_partkey, p_type
FROM part) AS t
WHERE p_type = 'PROMO BRUSHED COPPER') AS t1
INNER JOIN (SELECT l_orderkey, l_partkey, l_suppkey, l_extendedprice, l_discount
FROM lineitem) AS t2 ON t1.p_partkey = t2.l_partkey
-- 5
SELECT t0.s_suppkey, CASE WHEN t.n_name = 'EGYPT                    ' THEN 1 ELSE 0 END AS nation_check
FROM (SELECT n_nationkey, n_name
FROM nation) AS t
INNER JOIN (SELECT s_suppkey, s_nationkey
FROM supplier) AS t0 ON t.n_nationkey = t0.s_nationkey
