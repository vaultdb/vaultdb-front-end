package org.vaultdb.support;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class TpcHQueries {

    public static final List<String> QUERIES = ImmutableList.of(
            // 01
            "SELECT  l_returnflag,  l_linestatus,  SUM(l_quantity) as sum_qty, SUM(l_extendedprice) as sum_base_price, SUM(l_extendedprice * (1 - l_discount)) as sum_disc_price,  SUM(l_extendedprice * (1 - l_discount) * (1 + l_tax)) as sum_charge, AVG(l_quantity) as avg_qty,  AVG(l_extendedprice) as avg_price,  AVG(l_discount) as avg_disc,   COUNT(*) as count_order \n"
            + "FROM  lineitem \n"
            + "WHERE  l_shipdate <= date '1998-08-03' \n"
            +  "GROUP BY  l_returnflag, l_linestatus \n"
            + "ORDER BY  l_returnflag,  l_linestatus",

            // 02
            "WITH min_ps_supplycost AS ("
                    + "    select\n"
                    + "      ps.ps_partkey,min(ps.ps_supplycost) min_cost\n"
                    + "\n"
                    + "    from\n"
                    + "      partsupp ps,\n"
                    + "      supplier s,\n"
                    + "      nation n,\n"
                    + "      region r\n"
                    + "    where\n"
                    + "      s.s_suppkey = ps.ps_suppkey\n"
                    + "      and s.s_nationkey = n.n_nationkey\n"
                    + "      and n.n_regionkey = r.r_regionkey\n"
                    + "      and r.r_name = 'EUROPE'\n"
                    + "    GROUP BY ps.ps_partkey"
                    + "  )\n"
                    + "select\n"
                    + "  s.s_acctbal,\n"
                    + "  s.s_name,\n"
                    + "  n.n_name,\n"
                    + "  p.p_partkey,\n"
                    + "  p.p_mfgr,\n"
                    + "  s.s_address,\n"
                    + "  s.s_phone,\n"
                    + "  s.s_comment\n"
                    + "from\n"
                    + "  part p,\n"
                    + "  supplier s,\n"
                    + "  partsupp ps,\n"
                    + "  nation n,\n"
                    + "  region r,\n"
                    + "  min_ps_supplycost mps\n"
                    + "where\n"
                    + "  p.p_partkey = ps.ps_partkey\n"
                    + "  and s.s_suppkey = ps.ps_suppkey\n"
                    + "  and p.p_size = 41\n"
                    + "  and p.p_type like '%NICKEL'\n"
                    + "  and s.s_nationkey = n.n_nationkey\n"
                    + "  and n.n_regionkey = r.r_regionkey\n"
                    + "  and r.r_name = 'EUROPE'\n"
                    + "  and mps.ps_partkey = ps.ps_partkey\n"
                    + "  and ps.ps_supplycost = mps.min_cost\n"
                    + "\n"
                    + "order by\n"
                    + "  s.s_acctbal desc,\n"
                    + "  n.n_name,\n"
                    + "  s.s_name,\n"
                    + "  p.p_partkey\n"
                    + "limit 100",

            // 03
            "SELECT o_orderkey,  sum(l.l_extendedprice * (1 - l.l_discount)) as revenue, o_orderdate, o_shippriority "
            + " FROM customer c JOIN orders o ON  c.c_custkey = o.o_custkey\n"
            + "     JOIN lineitem l ON l.l_orderkey = o.o_orderkey\n"
            + " WHERE  c.c_mktsegment = 'HOUSEHOLD'  AND o.o_orderdate < date '1995-03-25' AND l.l_shipdate > date '1995-03-25' "
            + " GROUP BY  o_orderkey, o.o_orderdate,  o.o_shippriority\n"
            + " ORDER BY  revenue DESC, o.o_orderdate "
            + " LIMIT 10",

            // 04
            "SELECT o_orderpriority, COUNT(*) as order_count \n"
                    + "FROM  orders JOIN lineitem ON l_orderkey = o_orderkey \n"
                    + "WHERE o_orderdate >= date '1993-07-01' AND o_orderdate < date '1998-12-01' AND l_commitdate < l_receiptdate\n"
                    + "GROUP BY  o_orderpriority\n"
                    + "ORDER BY o_orderpriority\n",
            // 05
            "SELECT n.n_name, SUM(l.l_extendedprice * (1 - l.l_discount)) as revenue\n"
            + " FROM  region r JOIN nation n ON n_regionkey = r_regionkey "
            + "     JOIN customer c  ON c_nationkey = n_nationkey "
            + "     JOIN orders o ON c.c_custkey = o.o_custkey "
            + "     JOIN lineitem l ON l.l_orderkey = o.o_orderkey "
            + "     JOIN supplier s ON l.l_suppkey = s.s_suppkey "
            + "WHERE c.c_nationkey = s.s_nationkey  AND r.r_name = 'EUROPE' AND o.o_orderdate >= date '1993-01-01' AND o.o_orderdate < date '1994-01-01'\n"
            + " GROUP BY   n.n_name\n"
            + " ORDER BY revenue DESC",

            // 05
//            "WITH snr as (SELECT * from region r"
//                    + " JOIN nation n ON n.n_regionkey = r.r_regionkey"
//                    + " JOIN supplier s ON s.s_nationkey = n.n_nationkey"
//                    + "  where r.r_name = 'EUROPE'\n"
//                    + "),"
//            + "cnr as (SELECT * from region r"
//                    + " JOIN nation n ON n.n_regionkey = r.r_regionkey"
//                    + " JOIN customer c ON c.c_nationkey = n.n_nationkey"
//                    + "  where r.r_name = 'EUROPE'\n"
//                    + ")"
//            + "select\n"
//                    + "  cnr.n_name,\n"
//                    + "  sum(l.l_extendedprice * (1 - l.l_discount)) as revenue\n"
//                    + "\n"
//                    + "from\n"
//                    + "  cnr,\n"
//                    + "  orders o,\n"
//                    + "  lineitem l,\n"
//                    + "  snr\n"
//                    + "\n"
//                    + "where\n"
//                    + "  cnr.c_custkey = o.o_custkey\n"
//                    + "  and l.l_orderkey = o.o_orderkey\n"
//                    + "  and l.l_suppkey = snr.s_suppkey\n"
//                    + "  and cnr.c_nationkey = snr.s_nationkey\n"
//                    + "  and o.o_orderdate >= date '1997-01-01'\n"
//                    + "  and o.o_orderdate < date '1998-01-01'\n"
//                    + "group by\n"
//                    + "  cnr.n_name\n"
//                    + "\n"
//                    + "order by\n"
//                    + "  revenue desc",

            // 05
//            "WITH snr as (SELECT * from supplier s"
//                    + " JOIN nation n ON s.s_nationkey = n.n_nationkey"
//                    + " JOIN region r ON n.n_regionkey = r.r_regionkey"
//                    + "  where r.r_name = 'EUROPE'\n"
//                    + "),"
//                + "cnr as (SELECT * from customer c"
//                    + " JOIN nation n ON c.c_nationkey = n.n_nationkey"
//                    + " JOIN region r ON n.n_regionkey = r.r_regionkey"
//                    + "  where r.r_name = 'EUROPE'\n"
//                    + "),"
//                + "scnr as (select\n"
//                    + "  cnr.c_custkey, cnr.c_nationkey, snr.s_suppkey, snr.s_nationkey, cnr.n_name \n"
//                    + "\n"
//                    + "from\n"
//                    + "  cnr,\n"
//                    + "  snr\n"
//                    + "\n"
//                    + "where\n"
//                    + "  cnr.c_nationkey = snr.s_nationkey\n"
//                    + ")\n"
//                + "select\n"
//                    + "  scnr.n_name,\n"
//                    + "  sum(l.l_extendedprice * (1 - l.l_discount)) as revenue\n"
//                    + "\n"
//                    + "from\n"
//                    + "  scnr,\n"
//                    + "  orders o,\n"
//                    + "  lineitem l\n"
//                    + "\n"
//                    + "where\n"
//                    + "  scnr.c_custkey = o.o_custkey\n"
//                    + "  and l.l_orderkey = o.o_orderkey\n"
//                    + "  and l.l_suppkey = scnr.s_suppkey\n"
//                    + "  and o.o_orderdate >= date '1997-01-01'\n"
//                    + "  and o.o_orderdate < date '1998-01-01'\n"
//                    + "group by\n"
//                    + "  scnr.n_name\n"
//                    + "\n"
//                    + "order by\n"
//                    + "  revenue desc",

            // nested subquery for supplier, region, nation, where region = Europe

            // 06
            "select\n"
                    + "  sum(l_extendedprice * l_discount) as revenue\n"
                    + "from\n"
                    + "  lineitem\n"
                    + "where\n"
                    + "  l_shipdate >= date '1997-01-01'\n"
                    + "  and l_shipdate < date '1998-01-01'\n"
                    + "  and\n"
                    + "  l_discount between 0.03 - 0.01 and 0.03 + 0.01\n"
                    + "  and l_quantity < 24",

            // 07
            "WITH lineitems AS (\n"
                    + "     SELECT l_extendedprice, l_discount, EXTRACT(year from l_shipdate) l_year, l_shipdate, l_suppkey,	l_orderkey\n"
                    + "     FROM lineitem\n"
                    + "     WHERE  l_shipdate >= date '1995-01-01' and l_shipdate <= date '1996-12-31'),\n"
                    + "   shipping AS (\n"
                    + "      select\n"
                    + "      n1.n_name as supp_nation,\n"
                    + "      n2.n_name as cust_nation,\n"
                    + "      l_year,\n"
                    + "      l.l_extendedprice * (1 - l.l_discount) as volume\n"
                    + "    from\n"
                    + "      supplier s,\n"
                    + "      lineitems l,\n"
                    + "      orders o,\n"
                    + "      customer c,\n"
                    + "      nation n1,\n"
                    + "      nation n2\n"
                    + "    where\n"
                    + "      s.s_suppkey = l.l_suppkey\n"
                    + "      and o.o_orderkey = l.l_orderkey\n"
                    + "      and c.c_custkey = o.o_custkey\n"
                    + "      and s.s_nationkey = n1.n_nationkey\n"
                    + "      and c.c_nationkey = n2.n_nationkey\n"
                    + "      and (\n"
                    + "        (n1.n_name = 'EGYPT' and n2.n_name = 'UNITED STATES')\n"
                    + "        or (n1.n_name = 'UNITED STATES' and n2.n_name = 'EGYPT')\n"
                    + "      )\n"
                    + "  ) \n"
                    + "select\n"
                    + "  supp_nation, cust_nation, l_year, sum(volume) as revenue\n"
                    + "from shipping\n"
                    + "group by\n"
                    + "  supp_nation,\n"
                    + "  cust_nation,\n"
                    + "  l_year\n"
                    + "order by\n"
                    + "  supp_nation,\n"
                    + "  cust_nation,\n"
                    + "  l_year",
            // 08 - original
//            "SELECT   o_year, SUM(CASE WHEN  n_nation = 'KENYA' THEN volume ELSE 0.0 END) / SUM(VOLUME) AS mkt_share "
//            + "FROM ( SELECT CAST(o_orderyear AS INT) AS o_year, l.l_extendedprice * (1 - l.l_discount) AS volume,  n2.n_name as n_nation "
//            + "       FROM part p, supplier s, lineitem l, orders o, customer c, nation n1, nation n2, region r "
//            + "       WHERE p.p_partkey = l.l_partkey AND s.s_suppkey = l.l_suppkey AND l.l_orderkey = o.o_orderkey AND  o.o_custkey = c.c_custkey "
//            + "             AND c.c_nationkey = n1.n_nationkey AND n1.n_regionkey = r.r_regionkey AND r.r_name = 'AFRICA' AND s.s_nationkey = n2.n_nationkey "
//            + "             AND o.o_orderdate BETWEEN DATE '1995-01-01' AND DATE '1996-12-31' AND  p.p_type = 'LARGE ANODIZED STEEL') AS all_nations "
//            + "GROUP BY o_year "
//            + "ORDER BY o_year",

            // 08 - modified to make join keys easier to parse
            "WITH cnr AS (SELECT c_custkey "
               + "FROM customer JOIN nation ON c_nationkey = n_nationkey "
               + "  JOIN region ON n_regionkey = r_regionkey "
               + "WHERE r_name = 'AFRICA') "
            + "SELECT   o_year, SUM(CASE WHEN  n_nation = 'KENYA' THEN volume ELSE 0.0 END) / SUM(VOLUME) AS mkt_share "
                    + "FROM ( SELECT CAST(o_orderyear AS INT) AS o_year, l.l_extendedprice * (1 - l.l_discount) AS volume,  n.n_name as n_nation "
                    + "       FROM part p, supplier s, lineitem l, orders o, cnr c,  nation n "
                    + "       WHERE p.p_partkey = l.l_partkey AND s.s_suppkey = l.l_suppkey AND l.l_orderkey = o.o_orderkey AND  o.o_custkey = c.c_custkey "
                    + "             AND s.s_nationkey = n.n_nationkey "
                    + "             AND o.o_orderdate BETWEEN DATE '1995-01-01' AND DATE '1996-12-31' AND  p.p_type = 'LARGE ANODIZED STEEL') AS all_nations "
                    + "GROUP BY o_year "
                    + "ORDER BY o_year",

            // 09
            "WITH order_years AS (\n"
                    +  "     SELECT CAST(o_orderyear AS INT) as o_year, o.o_orderkey  \n"
                    + " FROM orders o),\n"
                    + "     yellow_parts AS (\n"
                    + "         SELECT p_partkey\n"
                    + "      FROM part\n"
                    + "      WHERE p_name like '%yellow%'),\n"
                    + "     profit AS (\n"
                    + "         SELECT   n_name, o_year, l.l_extendedprice * (1 - l.l_discount) - ps.ps_supplycost * l.l_quantity as amount"
                    + "       FROM     yellow_parts p, supplier s, lineitem l, partsupp ps,  order_years o,  nation n \n"
                    + "    WHERE  s.s_suppkey = l.l_suppkey AND ps.ps_suppkey = l.l_suppkey AND ps.ps_partkey = l.l_partkey AND p.p_partkey = l.l_partkey AND o.o_orderkey = l.l_orderkey AND s.s_nationkey = n.n_nationkey)"
                    + " SELECT n_name, o_year, SUM(amount) as sum_profit\n"
                    + " FROM profit\n"
                    + " GROUP BY n_name, o_year \n"
                    + " ORDER BY n_name, o_year DESC",

            // 10
            "select\n"
                    + "  c.c_custkey,\n"
                    + "  c.c_name,\n"
                    + "  sum(l.l_extendedprice * (1 - l.l_discount)) as revenue,\n"
                    + "  c.c_acctbal,\n"
                    + "  n.n_name,\n"
                    + "  c.c_address,\n"
                    + "  c.c_phone,\n"
                    + "  c.c_comment\n"
                    + "from\n"
                    + "  customer c,\n"
                    + "  orders o,\n"
                    + "  lineitem l,\n"
                    + "  nation n\n"
                    + "where\n"
                    + "  c.c_custkey = o.o_custkey\n"
                    + "  and l.l_orderkey = o.o_orderkey\n"
                    + "  and o.o_orderdate >= date '1994-03-01'\n"
                    + "  and o.o_orderdate < date '1994-06-01'\n"
                    + "  and l.l_returnflag = 'R'\n"
                    + "  and c.c_nationkey = n.n_nationkey\n"
                    + "group by\n"
                    + "  c.c_custkey,\n"
                    + "  c.c_name,\n"
                    + "  c.c_acctbal,\n"
                    + "  c.c_phone,\n"
                    + "  n.n_name,\n"
                    + "  c.c_address,\n"
                    + "  c.c_comment\n"
                    + "order by\n"
                    + "  revenue desc\n"
                    + "limit 20",

            // 11
            "WITH percentile AS (\n"
                    + "     SELECT SUM(psp.ps_supplycost * psp.ps_availqty) * 0.0001000000\n"
                    + "     FROM\n"
                    + "        partsupp psp,\n"
                    + "        supplier sp,\n"
                    + "        nation np\n"
                    + "      where\n"
                    + "        psp.ps_suppkey = sp.s_suppkey\n"
                    + "        and sp.s_nationkey = np.n_nationkey\n"
                    + "        and np.n_name = 'JAPAN'\n"
                    + "    )\n"
                    + "SELECT\n"
                    + "  ps.ps_partkey,\n"
                    + "  sum(ps.ps_supplycost * ps.ps_availqty) as val\n"
                    + "FROM\n"
                    + "  partsupp ps,\n"
                    + "  supplier s,\n"
                    + "  nation n\n"
                    + "WHERE\n"
                    + "  ps.ps_suppkey = s.s_suppkey\n"
                    + "  and s.s_nationkey = n.n_nationkey\n"
                    + "  and n.n_name = 'JAPAN'\n"
                    + "GROUP BY ps.ps_partkey \n"
                    + "HAVING sum(ps.ps_supplycost * ps.ps_availqty) > (SELECT * FROM percentile)\n"
                    + "ORDER BY val DESC",

            // 12
            "select\n"
                    + "  l.l_shipmode,\n"
                    + "  sum(case\n"
                    + "    when o.o_orderpriority = '1-URGENT'\n"
                    + "      or o.o_orderpriority = '2-HIGH'\n"
                    + "      then 1\n"
                    + "    else 0\n"
                    + "  end) as high_line_count,\n"
                    + "  sum(case\n"
                    + "    when o.o_orderpriority <> '1-URGENT'\n"
                    + "      and o.o_orderpriority <> '2-HIGH'\n"
                    + "      then 1\n"
                    + "    else 0\n"
                    + "  end) as low_line_count\n"
                    + "from\n"
                    + "  orders o,\n"
                    + "  lineitem l\n"
                    + "where\n"
                    + "  o.o_orderkey = l.l_orderkey\n"
                    + "  and l.l_shipmode in ('TRUCK', 'REG AIR')\n"
                    + "  and l.l_commitdate < l.l_receiptdate\n"
                    + "  and l.l_shipdate < l.l_commitdate\n"
                    + "  and l.l_receiptdate >= date '1994-01-01'\n"
                    + "  and l.l_receiptdate < date '1995-01-01'\n"
                    + "group by\n"
                    + "  l.l_shipmode\n"
                    + "order by\n"
                    + "  l.l_shipmode",

            // 13
            "WITH c_orders AS (\n"
                    + "     SELECT c.c_custkey, COUNT(o.o_orderkey) c_count   \n"
                    + "     FROM   customer c  LEFT OUTER JOIN orders o ON c.c_custkey = o.o_custkey  AND o.o_comment NOT LIKE '%special%requests%'   \n"
                    + "     GROUP BY   c.c_custkey )\n"
                    + "SELECT  c_count,   count(*) as custdist \n"
                    + "FROM    c_orders\n"
                    + "GROUP BY c_count \n"
                    + "ORDER BY custdist DESC, c_count DESC",

            // 14
            "select\n"
                    + "  100.00 * sum(case\n"
                    + "    when p.p_type like 'PROMO%'\n"
                    + "      then l.l_extendedprice * (1 - l.l_discount)\n"
                    + "    else 0\n"
                    + "  end) / sum(l.l_extendedprice * (1 - l.l_discount)) as promo_revenue\n"
                    + "from\n"
                    + "  lineitem l,\n"
                    + "  part p\n"
                    + "where\n"
                    + "  l.l_partkey = p.p_partkey\n"
                    + "  and l.l_shipdate >= date '1994-08-01'\n"
                    + "  and l.l_shipdate < date '1994-09-01'",

            // 15
            "with revenue0 (supplier_no, total_revenue) as (\n"
                    + "  select\n"
                    + "    l_suppkey,\n"
                    + "    sum(l_extendedprice * (1 - l_discount))\n"
                    + "  from\n"
                    + "    lineitem\n"
                    + "  where\n"
                    + "    l_shipdate >= date '1993-05-01'\n"
                    + "    and l_shipdate < date '1993-08-01'\n"
                    + "  group by\n"
                    + "    l_suppkey)\n"
                    + "select\n"
                    + "  s.s_suppkey,\n"
                    + "  s.s_name,\n"
                    + "  s.s_address,\n"
                    + "  s.s_phone,\n"
                    + "  r.total_revenue\n"
                    + "from\n"
                    + "  supplier s,\n"
                    + "  revenue0 r\n"
                    + "where\n"
                    + "  s.s_suppkey = r.supplier_no\n"
                    + "  and r.total_revenue = (\n"
                    + "    select\n"
                    + "      max(total_revenue)\n"
                    + "    from\n"
                    + "      revenue0\n"
                    + "  )\n"
                    + "order by\n"
                    + "  s.s_suppkey",

            // 16
            // breaking this into smaller pieces for easier debugging
            "WITH complaints AS (SELECT * FROM supplier WHERE s_comment LIKE '%Customer%Complaints%'),\n" +
                    "     ps_suppkey_set_diff AS (\n" +
                    "     SELECT ps_partkey, ps_suppkey\n" +
                    "     FROM partsupp ps LEFT JOIN complaints c ON ps.ps_suppkey = c.s_suppkey\n" +
                    "     WHERE c.s_suppkey IS NULL),\n" +
                    "\n" +
                    "     part_pro  AS ( SELECT p_partkey, p_brand,p_type,p_size " +
                    "   FROM part p \n" +
                    "      WHERE p.p_brand <> 'Brand#21'\n" +
                    "      AND p.p_type not like 'MEDIUM PLATED%'\n" +
                    "        AND p.p_size IN (38, 2, 8, 31, 44, 5, 14, 24)\n" +
                    "      )\n" +
                    "\n" +
                    "SELECT\n" +
                    "   p.p_brand,\n" +
                    "   p.p_type,\n" +
                    "   p.p_size,\n" +
                    "   COUNT(DISTINCT ps.ps_suppkey) as supplier_cnt\n" +
                    " FROM\n" +
                    "   ps_suppkey_set_diff ps,\n" +
                    "   part_pro p\n" +
                    " WHERE\n" +
                    "   p.p_partkey = ps.ps_partkey\n" +
                    "   \n" +
                    " GROUP BY\n" +
                    "   p.p_brand,\n" +
                    "   p.p_type,\n" +
                    "   p.p_size\n" +
                    " ORDER BY\n" +
                    "   supplier_cnt desc,\n" +
                    "   p.p_brand,\n" +
                    "   p.p_type,\n" +
                    "   p.p_size",

            // 17
            "SELECT sum(extendedprice) / 7.0 as avg_yearly \n"
                    + "FROM ( \n"
                    + "     SELECT l_quantity as quantity, l_extendedprice as extendedprice, t_avg_quantity\n"
                    + "     FROM (\n"
                    + "       SELECT l_partkey as t_partkey, 0.2 * avg(l_quantity) as t_avg_quantity \n"
                    + "       FROM lineitem\n"
                    + "       GROUP By l_partkey) as tmp\n"
                    + "    INNER JOIN (\n"
                    + "      SELECT l_quantity, l_partkey, l_extendedprice\n"
                    + "      FROM  part, lineitem \n"
                    + "      WHERE  p_partkey = l_partkey and p_brand = 'Brand#23' and p_container = 'MED BOX'\n"
                    + "      ) as l1 ON  l1.l_partkey = t_partkey ) a\n"
                    + "where quantity < t_avg_quantity\n",

            // 18 - original
//            "select c.c_name, c.c_custkey, o.o_orderkey, o.o_orderdate, o.o_totalprice, sum(l.l_quantity) \n"
//            + "from  customer c,  orders o, lineitem l \n"
//            + "where  o.o_orderkey in (\n"
//            + "    select l_orderkey \n"
//            + "    from lineitem \n"
//            + "    group by  l_orderkey "
//            + "    having  sum(l_quantity) > 7) \n"
//            + "  and c.c_custkey = o.o_custkey  and o.o_orderkey = l.l_orderkey \n"
//            + "group by  c.c_name,  c.c_custkey, o.o_orderkey, o.o_orderdate, o.o_totalprice\n"
//            + "order by  o.o_totalprice desc, o.o_orderdate "
//            + "limit 100",

            // 18 - modified to avoid naming collision on l_orderkey
            "WITH selected_orders AS (" +
                    "SELECT o_orderkey, o_orderdate, o_totalprice, o_custkey " +
                    "FROM orders " +
                    "WHERE o_orderkey IN (" +
                    "    SELECT l_orderkey " +
                    "    FROM lineitem " +
                    "    GROUP BY l_orderkey " +
                    "    HAVING SUM(l_quantity) > 7)) " +
            "select c.c_name, c.c_custkey, o.o_orderkey, o.o_orderdate, o.o_totalprice, sum(l.l_quantity) \n"
            + "from  customer c,  selected_orders o, lineitem l \n"
                    + "where c.c_custkey = o.o_custkey  and o.o_orderkey = l.l_orderkey \n"
                    + "group by  c.c_name,  c.c_custkey, o.o_orderkey, o.o_orderdate, o.o_totalprice\n"
                    + "order by  o.o_totalprice desc, o.o_orderdate "
                    + "limit 100",
            // 19
            "select\n"
                    + "  sum(l.l_extendedprice* (1 - l.l_discount)) as revenue\n"
                    + "from\n"
                    + "  lineitem l,\n"
                    + "  part p\n"
                    + "where\n"
                    + "  (\n"
                    + "    p.p_partkey = l.l_partkey\n"
                    + "    and p.p_brand = 'Brand#41'\n"
                    + "    and p.p_container in ('SM CASE', 'SM BOX', 'SM PACK', 'SM PKG')\n"
                    + "    and l.l_quantity >= 2 and l.l_quantity <= 2 + 10\n"
                    + "    and p.p_size between 1 and 5\n"
                    + "    and l.l_shipmode in ('AIR', 'AIR REG')\n"
                    + "    and l.l_shipinstruct = 'DELIVER IN PERSON'\n"
                    + "  )\n"
                    + "  or\n"
                    + "  (\n"
                    + "    p.p_partkey = l.l_partkey\n"
                    + "    and p.p_brand = 'Brand#13'\n"
                    + "    and p.p_container in ('MED BAG', 'MED BOX', 'MED PKG', 'MED PACK')\n"
                    + "    and l.l_quantity >= 14 and l.l_quantity <= 14 + 10\n"
                    + "    and p.p_size between 1 and 10\n"
                    + "    and l.l_shipmode in ('AIR', 'AIR REG')\n"
                    + "    and l.l_shipinstruct = 'DELIVER IN PERSON'\n"
                    + "  )\n"
                    + "  or\n"
                    + "  (\n"
                    + "    p.p_partkey = l.l_partkey\n"
                    + "    and p.p_brand = 'Brand#55'\n"
                    + "    and p.p_container in ('LG CASE', 'LG BOX', 'LG PACK', 'LG PKG')\n"
                    + "    and l.l_quantity >= 23 and l.l_quantity <= 23 + 10\n"
                    + "    and p.p_size between 1 and 15\n"
                    + "    and l.l_shipmode in ('AIR', 'AIR REG')\n"
                    + "    and l.l_shipinstruct = 'DELIVER IN PERSON'\n"
                    + "  )",

            // 20
            "WITH antiques AS (SELECT p.p_partkey\n"
                    + "                                 from\n"
                    + "                                   part p\n"
                    + "                                 where\n"
                    + "                                   p.p_name like 'antique%'\n"
                    + "                                ),\n"
                    + "            lineitem_agg AS (\n"
                    + "                            select\n"
                    + "                                   l_partkey, l_suppkey, 0.5 * sum(l.l_quantity) inventory\n"
                    + "                                 from\n"
                    + "                                   lineitem l\n"
                    + "                                 where\n"
                    + "                                    l.l_shipdate >= date '1993-01-01'\n"
                    + "                                   and l.l_shipdate < date '1994-01-01'\n"
                    + "                              GROUP BY l_partkey, l_suppkey\n"
                    + "                     ),\n"
                    + "\n"
                    + "            ps_qualified AS (\n"
                    + "	        SELECT ps_suppkey, ps_partkey, ps.ps_availqty\n"
                    + "                FROM  partsupp ps LEFT JOIN antiques a ON ps.ps_partkey = a.p_partkey\n"
                    + "                 WHERE  a.p_partkey IS NOT NULL),\n"
                    + "            qualified AS (\n"
                    + "                          SELECT DISTINCT ps.ps_suppkey\n"
                    + "                          FROM lineitem_agg la INNER JOIN ps_qualified ps ON la.l_partkey = ps.ps_partkey AND la.l_suppkey = ps.ps_suppkey\n"
                    + "                           WHERE ps.ps_availqty > la.inventory\n"
                    + "                            )\n"
                    + "select\n"
                    + "                        s.s_name,\n"
                    + "                        s.s_address\n"
                    + "                      from\n"
                    + "                        supplier s LEFT JOIN qualified q ON s.s_suppkey = q.ps_suppkey,\n"
                    + "                        nation n\n"
                    + "                      where\n"
                    + "                        ps_suppkey IS NOT NULL\n"
                    + "                        and s.s_nationkey = n.n_nationkey\n"
                    + "                        and n.n_name = 'KENYA'\n"
                    + "                      order by\n"
                    + "                        s.s_name\n",

            //21
            " WITH l2 AS ( select DISTINCT l_suppkey, l_orderkey\n"
                    + "                                              from\n"
                    + "                                                lineitem l1),\n"
                    + "\n"
                    + "\n"
                    + " l1prime AS (SELECT DISTINCT l1.l_suppkey, l1.l_orderkey \n"
                    + "   FROM lineitem l1 LEFT JOIN l2 ON (l2.l_orderkey = l1.l_orderkey\n"
                    + " AND l2.l_suppkey <> l1.l_suppkey)\n"
                    + "                                       WHERE l2.l_orderkey IS NOT NULL),\n"
                    + "\n"
                    + "\n"
                    + "  l3 AS (\n"
                    + "       select DISTINCT  l_orderkey, l_suppkey\n"
                    + "      from lineitem l3\n"
                    + "      where l3.l_receiptdate > l3.l_commitdate\n"
                    + "   ),\n"
                    + "\n"
                    + "  l1doubleprime AS (SELECT l1prime.l_suppkey, l1prime.l_orderkey, l1prime.l_receiptdate, l1prime.l_commitdate\n"
                    + "     FROM lineitem l1prime LEFT JOIN l3 ON l3.l_orderkey = l1prime.l_orderkey  and l3.l_suppkey <> l1prime.l_suppkey\n"
                    + "     WHERE  l3.l_orderkey IS NULL),\n"
                    + "\n"
                    + " agg AS (select\n"
                    + "    s.s_name, l_orderkey, l_suppkey\n"
                    + "  from\n"
                    + "    supplier s,\n"
                    + "    l1doubleprime l1,\n"
                    + "    orders o,\n"
                    + "    nation n\n"
                    + "  where\n"
                    + "    s.s_suppkey = l1.l_suppkey\n"
                    + "    and o.o_orderkey = l1.l_orderkey\n"
                    + "    and o.o_orderstatus = 'F'\n"
                    + "    and l1.l_receiptdate > l1.l_commitdate\n"
                    + "    and s.s_nationkey = n.n_nationkey\n"
                    + "    and n.n_name = 'BRAZIL'\n"
                    + "  order by\n"
                    + "    s.s_name)\n"
                    + "\n"
                    + " SELECT agg.s_name, count(*) as numwait FROM agg LEFT JOIN l1prime on agg.l_orderkey = l1prime.l_orderkey and agg.l_suppkey = l1prime.l_suppkey WHERE l1prime.l_suppkey IS NOT NULL\n"
                    + " group by\n"
                    + "   agg.s_name\n"
                    + " order by\n"
                    + "   numwait desc,\n"
                    + "   agg.s_name\n"
                    + " limit 100",

            // 22
            "WITH customer_selection AS (\n"
                    + "       SELECT substring(c_phone from 1 for 2) as cntrycode, c_acctbal, c_custkey\n"
                    + "       FROM customer\n"
                    + "       WHERE substring(c_phone from 1 for 2) in\n"
                    + "            ('24', '31', '11', '16', '21', '20', '34')),\n"
                    + "    c_avg_acctbal AS (\n"
                    + "	      SELECT avg(c_acctbal) \n"
                    + "	      FROM customer_selection\n"
                    + "	      WHERE c_acctbal > 0.0)\n"
                    + "SELECT cntrycode, count(*) as numcust, sum(c_acctbal) as totacctbal\n"
                    + "FROM customer_selection c LEFT OUTER JOIN orders o  ON o.o_custkey = c.c_custkey\n"
                    + "WHERE c_acctbal > (SELECT * FROM c_avg_acctbal) AND o.o_custkey IS NULL\n"
                    + "GROUP BY cntrycode\n"
                    + "ORDER BY cntrycode\n"

    );
}
