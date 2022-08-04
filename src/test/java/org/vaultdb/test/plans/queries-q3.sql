-- 0
SELECT c_custkey, NOT c_mktsegment = 'HOUSEHOLD ' AS dummy_tag
FROM (SELECT c_custkey, c_mktsegment
FROM customer) AS t
ORDER BY c_custkey
-- 1
SELECT *, NOT o_orderdate < DATE '1995-03-25' AS dummy_tag
FROM (SELECT o_orderkey, o_custkey, o_orderdate, o_shippriority
FROM orders) AS t
ORDER BY o_orderkey, o_custkey, o_orderdate, o_shippriority
-- 3
SELECT l_orderkey, l_extendedprice * (1 - l_discount) AS revenue, NOT l_shipdate > DATE '1995-03-25' AS dummy_tag
FROM (SELECT l_orderkey, l_extendedprice, l_discount, l_shipdate
FROM lineitem) AS t
ORDER BY l_orderkey, l_extendedprice, l_discount, l_shipdate
