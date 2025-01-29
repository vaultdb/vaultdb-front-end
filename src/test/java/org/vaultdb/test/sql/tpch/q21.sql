  WITH l2 AS ( select DISTINCT l_suppkey, l_orderkey 
                                               from 
                                                 lineitem l1), 
  
  
  l1prime AS (SELECT DISTINCT l1.l_suppkey, l1.l_orderkey  
    FROM lineitem l1 LEFT JOIN l2 ON (l2.l_orderkey = l1.l_orderkey AND l2.l_suppkey <> l1.l_suppkey)
    WHERE l2.l_orderkey IS NOT NULL),
   l3 AS ( 
        select DISTINCT  l_orderkey, l_suppkey 
       from lineitem l3 
       where l3.l_receiptdate > l3.l_commitdate 
    ), 
  
   l1doubleprime AS (SELECT l1prime.l_suppkey, l1prime.l_orderkey, l1prime.l_receiptdate, l1prime.l_commitdate 
      FROM lineitem l1prime LEFT JOIN l3 ON l3.l_orderkey = l1prime.l_orderkey  and l3.l_suppkey <> l1prime.l_suppkey 
      WHERE  l3.l_orderkey IS NULL), 
  
  agg AS (select 
     s.s_name, l_orderkey, l_suppkey 
   from 
     supplier s, 
     l1doubleprime l1, 
     orders o, 
     nation n 
   where 
     s.s_suppkey = l1.l_suppkey 
     and o.o_orderkey = l1.l_orderkey 
     and o.o_orderstatus = 'F' 
     and l1.l_receiptdate > l1.l_commitdate 
     and s.s_nationkey = n.n_nationkey 
     and n.n_name = 'BRAZIL' 
   order by 
     s.s_name) 
  
  SELECT agg.s_name, count(*) as numwait FROM agg LEFT JOIN l1prime on agg.l_orderkey = l1prime.l_orderkey and agg.l_suppkey = l1prime.l_suppkey WHERE l1prime.l_suppkey IS NOT NULL 
  group by 
    agg.s_name 
  order by 
    numwait desc, 
    agg.s_name 
  limit 100