-- 0
SELECT t2.n_name, t3.c_custkey, t3.c_nationkey
FROM (SELECT r_regionkey
FROM (SELECT r_regionkey, r_name
FROM region) AS t
WHERE r_name = 'EUROPE                   ') AS t1
INNER JOIN (SELECT n_nationkey, n_name, n_regionkey
FROM nation) AS t2 ON t1.r_regionkey = t2.n_regionkey
INNER JOIN (SELECT c_custkey, c_nationkey
FROM customer) AS t3 ON t2.n_nationkey = t3.c_nationkey
-- 1
SELECT o_orderkey, o_custkey, NOT (o_orderdate >= DATE '1997-01-01' AND o_orderdate < DATE '1998-01-01') AS dummy_tag
FROM (SELECT o_orderkey, o_custkey, o_orderdate
FROM orders) AS t
ORDER BY o_orderkey, o_custkey, o_orderdate
-- 3
SELECT l_orderkey, l_suppkey, l_extendedprice, l_discount
FROM lineitem
ORDER BY l_orderkey, l_suppkey, l_extendedprice, l_discount
-- 5
SELECT t3.s_suppkey, t3.s_nationkey
FROM (SELECT r_regionkey
FROM (SELECT r_regionkey, r_name
FROM region) AS t
WHERE r_name = 'EUROPE                   ') AS t1
INNER JOIN (SELECT n_nationkey, n_regionkey
FROM nation) AS t2 ON t1.r_regionkey = t2.n_regionkey
INNER JOIN (SELECT s_suppkey, s_nationkey
FROM supplier) AS t3 ON t2.n_nationkey = t3.s_nationkey
