SELECT
  SUM(l_extendedprice * l_discount) as revenue
FROM lineitem
WHERE  l_shipdate >= date '1997-01-01'
  and l_shipdate < date '1998-01-01'
  and
  l_discount between 0.03 - 0.01 and 0.03 + 0.01
  and l_quantity < 24