WITH lineitems AS (
     SELECT l_extendedprice, l_discount, EXTRACT(year from l_shipdate) l_year, l_shipdate, l_suppkey,	l_orderkey
     FROM lineitem
     WHERE  l_shipdate >= date '1995-01-01' and l_shipdate <= date '1996-12-31'),
   shipping AS (
      select
      n1.n_name as supp_nation,
      n2.n_name as cust_nation,
      l_year,
      l.l_extendedprice * (1 - l.l_discount) as volume
    from
      supplier s,
      lineitems l,
      orders o,
      customer c,
      nation n1,
      nation n2
    where
      s.s_suppkey = l.l_suppkey
      and o.o_orderkey = l.l_orderkey
      and c.c_custkey = o.o_custkey
      and s.s_nationkey = n1.n_nationkey
      and c.c_nationkey = n2.n_nationkey
      and (
        (n1.n_name = 'EGYPT' and n2.n_name = 'UNITED STATES')
        or (n1.n_name = 'UNITED STATES' and n2.n_name = 'EGYPT')
      )
  ) 
select
  supp_nation, cust_nation, l_year, sum(volume) as revenue
from shipping
group by
  supp_nation,
  cust_nation,
  l_year
order by
  supp_nation,
  cust_nation,
  l_year