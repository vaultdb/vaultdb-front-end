with revenue0 (supplier_no, total_revenue) as (
  select
    l_suppkey,
    sum(l_extendedprice * (1 - l_discount))
  from
    lineitem
  where
    l_shipdate >= date '1993-05-01'
    and l_shipdate < date '1993-08-01'
  group by
    l_suppkey)
select
  s.s_suppkey,
  s.s_name,
  s.s_address,
  s.s_phone,
  r.total_revenue
from
  supplier s,
  revenue0 r
where
  s.s_suppkey = r.supplier_no
  and r.total_revenue = (
    select
      max(total_revenue)
    from
      revenue0
  )
order by
  s.s_suppkey