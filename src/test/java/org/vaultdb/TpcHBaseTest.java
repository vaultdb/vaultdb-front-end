package org.vaultdb;

import java.util.*;
import java.util.logging.Logger;

import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.sql.SqlDialect;
import org.vaultdb.config.SystemConfiguration;
import org.vaultdb.config.WorkerConfiguration;
import org.vaultdb.db.schema.SystemCatalog;
import org.vaultdb.executor.config.ConnectionManager;
import org.vaultdb.parser.SqlStatementParser;
import org.vaultdb.support.TpcHQueries;
import org.vaultdb.util.Utilities;

import com.google.common.collect.ImmutableMap;

import junit.framework.TestCase;

public abstract class TpcHBaseTest  extends TestCase {

	protected SqlStatementParser parser;
	protected RelRoot relRoot;
	protected SqlDialect dialect;
	protected WorkerConfiguration honestBroker;
	protected Logger logger;
	protected SystemConfiguration config;
	protected SystemCatalog catalog;


	protected static final List<String> QUERIES = TpcHQueries.QUERIES;



	protected static final Map<String, String> TABLE_DEFINITIONS = ImmutableMap.<String, String>builder()
			.put("customer",
					"CREATE TABLE customer (\n" +
							"    c_custkey integer NOT NULL, \n" +
							"    c_name character varying(25), \n" +
							"    c_address character varying(40), \n" +
							"    c_nationkey integer, \n" +
							"    c_phone character(15), \n" +
							"    c_acctbal numeric, \n " +
							"    c_mktsegment character(10), \n" +
							"    c_comment character varying(117), \n" +
							"    CONSTRAINT c_acctbal_domain CHECK (c_acctbal >= CAST('-999.99' AS numeric) AND c_acctbal <= 9999.99), \n" +
							"    CONSTRAINT c_custkey_domain CHECK (c_custkey >= 1 AND c_custkey <= 15000), \n" +
							"    CONSTRAINT c_mktsegment_domain CHECK (c_mktsegment IN ('MACHINERY', 'AUTOMOBILE', 'BUILDING', 'FURNITURE', 'HOUSEHOLD')), \n" +
							"    CONSTRAINT c_nationkey_domain CHECK (c_nationkey >= 0 AND c_nationkey <= 24)\n" +
							")")
			.put("lineitem",
					"CREATE TABLE lineitem (\n" +
							"    l_orderkey integer NOT NULL, \n" +
							"    l_partkey integer, \n" +
							"    l_suppkey integer, \n" +
							"    l_linenumber integer NOT NULL, \n" +
							"    l_quantity numeric, \n" +
							"    l_extendedprice numeric, \n" +
							"    l_discount numeric, \n" +
							"    l_tax numeric, \n" +
							"    l_returnflag character(1), \n" +
							"    l_linestatus character(1), \n" +
							"    l_shipdate date, \n" +
							"    l_commitdate date, \n" +
							"    l_receiptdate date, \n" +
							"    l_shipinstruct character(25), \n" +
							"    l_shipmode character(10), \n" +
							"    l_comment character varying(44), \n" +
							"    CONSTRAINT l_discount_domain CHECK (((l_discount >= 0.00) AND (l_discount <= 0.10))), \n" +
							"    CONSTRAINT l_linenumber_domain CHECK (((l_linenumber >= 1) AND (l_linenumber <= 7))), \n" +
							//   restricting scope to single column constraints for now
							// "    CONSTRAINT l_linestatus_based_on_shipdate_domain CHECK ((((l_linestatus = 'O')) AND (l_shipdate > CAST('1995-06-17' AS date))) OR (l_linestatus = 'F' AND (l_shipdate <= CAST('1995-06-17' AS date))))," +
							// "    CONSTRAINT l_receiptdate_domain CHECK (((l_receiptdate >= (l_shipdate + INTERVAL '1' DAY))) AND (l_receiptdate <= (l_shipdate + INTERVAL '30' DAY))), " +
							// "    CONSTRAINT l_returnflag_based_on_receiptdate_domain CHECK ((l_returnflag IN ('R', 'A') AND l_receiptdate <= CAST('1995-06-17' AS date)) OR (l_returnflag = 'N' AND l_receiptdate > CAST('1995-06-17' AS date)))," +

							"    CONSTRAINT l_linestatus_domain CHECK (l_linestatus IN ('O', 'F')), \n" +
							"    CONSTRAINT l_partkey_domain CHECK ((l_partkey >= 1) AND (l_partkey <= 20000)), \n" +
							"    CONSTRAINT l_quantity_domain CHECK ((l_quantity >= 1) AND (l_quantity <= 50)), \n" +
							"    CONSTRAINT l_returnflag_domain CHECK (l_returnflag IN ('R', 'A', 'N')), \n" +
							"    CONSTRAINT l_shipinstruct_domain CHECK (l_shipinstruct IN ('DELIVER IN PERSON', 'COLLECT COD', 'NONE','TAKE BACK RETURN')), \n" +
							"    CONSTRAINT l_shipmode_domain CHECK (l_shipmode IN ('REG AIR', 'AIR', 'RAIL', 'SHIP', 'TRUCK', 'MAIL', 'FOB')), \n" +
							"    CONSTRAINT l_tax_domain CHECK (l_tax >= 0.00 AND l_tax <= 0.08) \n" +
							")")


			.put("nation",
					"CREATE TABLE nation (\n" +
							"    n_nationkey integer NOT NULL,\n" +
							"    n_name character(25),\n" +
							"    n_regionkey integer,\n" +
							"    n_comment character varying(152),\n" +
//							"    CONSTRAINT n_name_domain CHECK (n_name IN ('ETHIOPIA', 'IRAN', 'EGYPT', 'RUSSIA', 'SAUDI ARABIA', 'INDONESIA', 'VIETNAM', 'GERMANY', 'PERU', 'FRANCE', 'ALGERIA', 'ROMANIA', 'JORDAN', 'ARGENTINA', 'MOROCCO', 'CANADA', 'JAPAN', 'INDIA', 'UNITED KINGDOM', 'UNITED STATES', 'MOZAMBIQUE', 'CHINA', 'BRAZIL', 'KENYA', 'IRAQ')),\n" +
							"    CONSTRAINT n_nationkey_domain CHECK (((n_nationkey >= 0) AND (n_nationkey <= 24))),\n" +
//							"    CONSTRAINT n_nationkey_name_regionkey_domain CHECK ((((n_nationkey = 0) AND (n_name = CAST('ALGERIA' AS VARCHAR)) AND (n_regionkey = 0)) OR ((n_nationkey = 1) AND (n_name = CAST('ARGENTINA' AS VARCHAR)) AND (n_regionkey = 1)) OR ((n_nationkey = 2) AND (n_name = CAST('BRAZIL' AS VARCHAR)) AND (n_regionkey = 1)) OR ((n_nationkey = 3) AND (n_name = CAST('CANADA' AS VARCHAR)) AND (n_regionkey = 1)) OR ((n_nationkey = 4) AND (n_name = CAST('EGYPT' AS VARCHAR)) AND (n_regionkey = 4)) OR ((n_nationkey = 5) AND (n_name = CAST('ETHIOPIA' AS VARCHAR)) AND (n_regionkey = 0)) OR ((n_nationkey = 6) AND (n_name = CAST('FRANCE' AS VARCHAR)) AND (n_regionkey = 3)) OR ((n_nationkey = 7) AND (n_name = CAST('GERMANY' AS VARCHAR)) AND (n_regionkey = 3)) OR ((n_nationkey = 8) AND (n_name = CAST('INDIA' AS VARCHAR)) AND (n_regionkey = 2)) OR ((n_nationkey = 9) AND (n_name = CAST('INDONESIA' AS VARCHAR)) AND (n_regionkey = 2)) OR ((n_nationkey = 10) AND (n_name = CAST('IRAN' AS VARCHAR)) AND (n_regionkey = 4)) OR ((n_nationkey = 11) AND (n_name = CAST('IRAQ' AS VARCHAR)) AND (n_regionkey = 4)) OR ((n_nationkey = 12) AND (n_name = CAST('JAPAN' AS VARCHAR)) AND (n_regionkey = 2)) OR ((n_nationkey = 13) AND (n_name = CAST('JORDAN' AS VARCHAR)) AND (n_regionkey = 4)) OR ((n_nationkey = 14) AND (n_name = CAST('KENYA' AS VARCHAR)) AND (n_regionkey = 0)) OR ((n_nationkey = 15) AND (n_name = CAST('MOROCCO' AS VARCHAR)) AND (n_regionkey = 0)) OR ((n_nationkey = 16) AND (n_name = CAST('MOZAMBIQUE' AS VARCHAR)) AND (n_regionkey = 0)) OR ((n_nationkey = 17) AND (n_name = CAST('PERU' AS VARCHAR)) AND (n_regionkey = 1)) OR ((n_nationkey = 18) AND (n_name = CAST('CHINA' AS VARCHAR)) AND (n_regionkey = 2)) OR ((n_nationkey = 19) AND (n_name = CAST('ROMANIA' AS VARCHAR)) AND (n_regionkey = 3)) OR ((n_nationkey = 20) AND (n_name = CAST('SAUDI ARABIA' AS VARCHAR)) AND (n_regionkey = 4)) OR ((n_nationkey = 21) AND (n_name = CAST('VIETNAM' AS VARCHAR)) AND (n_regionkey = 2)) OR ((n_nationkey = 22) AND (n_name = CAST('RUSSIA' AS VARCHAR)) AND (n_regionkey = 3)) OR ((n_nationkey = 23) AND (n_name = CAST('UNITED KINGDOM' AS VARCHAR)) AND (n_regionkey = 3)) OR ((n_nationkey = 24) AND (n_name = CAST('UNITED STATES' AS VARCHAR)) AND (n_regionkey = 1)))),\n" +
							"    CONSTRAINT n_regionkey_domain CHECK (((n_regionkey >= 0) AND (n_regionkey <= 4)))\n" +
							")")

			.put("orders",
					"CREATE TABLE public.orders (\n" +
							"    o_orderkey integer NOT NULL,\n" +
							"    o_custkey integer,\n" +
							"    o_orderstatus character(1),\n" +
							"    o_totalprice numeric,\n" +
							"    o_orderdate date,\n" +
							"    o_orderpriority character(15),\n" +
							"    o_clerk character(15),\n" +
							"    o_shippriority integer,\n" +
							"    o_comment character varying(79),\n" +
//							"    CONSTRAINT o_clerk_domain CHECK (((split_part((o_clerk) AS text, '#'::text, 1) = 'Clerk'::text) AND (((split_part((o_clerk)::text, '#'::text, 2))::integer >= 1) AND ((split_part((o_clerk)::text, '#'::text, 2))::integer <= 1000)) AND (length(split_part((o_clerk)::text, '#'::text, 2)) = 9) AND (array_length(string_to_array((o_clerk)::text, '#'::text), 1) = 2))),\n" +
							"    CONSTRAINT o_orderdate_domain CHECK ((o_orderdate >= CAST('1992-01-01' AS date)) AND (o_orderdate <= CAST('1998-12-31' AS date))),\n" +
							"    CONSTRAINT o_orderkey_domain CHECK (((o_orderkey >= 1) AND (o_orderkey <= 600000))),\n" +
							"    CONSTRAINT o_orderpriority_domain CHECK ((o_orderpriority IN ('1-URGENT', '2-HIGH', '3-MEDIUM', '4-NOT SPECIFIED', '5-LOW'))),\n" +
							"    CONSTRAINT o_orderstatus_domain CHECK ((o_orderstatus IN ('F', 'O', 'P'))),\n" +
							"    CONSTRAINT o_shippriority_domain CHECK ((o_shippriority = 0))\n" +
							")")





			.put("part",
					"CREATE TABLE part (\n" +
							"    p_partkey integer NOT NULL,\n" +
							"    p_name character varying(55),\n" +
							"    p_mfgr character(25),\n" +
							"    p_brand character(10),\n" +
							"    p_type character varying(25),\n" +
							"    p_size integer,\n" +
							"    p_container character(10),\n" +
							"    p_retailprice numeric,\n" +
							"    p_comment character varying(23),\n" +
//							"    CONSTRAINT p_brand_domain CHECK (((split_part((p_brand)::text, '#'::text, 1) = 'Brand'::text) AND ((\"left\"(split_part((p_brand)::text, '#'::text, 2), 1))::integer = (split_part((p_mfgr)::text, '#'::text, 2))::integer) AND (((\"right\"(split_part((p_brand)::text, '#'::text, 2), 1))::integer >= 1) AND ((\"right\"(split_part((p_brand)::text, '#'::text, 2), 1))::integer <= 5)) AND (length(split_part((p_brand)::text, '#'::text, 2)) = 2) AND (array_length(string_to_array((p_brand)::text, '#'::text), 1) = 2))),\n" +
//							"    CONSTRAINT p_container_domain CHECK (((split_part((p_container)::text, ' '::text, 1) = ANY (ARRAY['JUMBO'::text, 'LG'::text, 'MED'::text, 'SM'::text, 'WRAP'::text])) AND (split_part((p_container)::text, ' '::text, 2) = ANY (ARRAY['BAG'::text, 'BOX'::text, 'CAN'::text, 'CASE'::text, 'DRUM'::text, 'JAR'::text, 'PACK'::text, 'PKG'::text])) AND (array_length(string_to_array((p_container)::text, ' '::text), 1) = 2))),\n" +
//							"    CONSTRAINT p_mfgr_domain CHECK (((split_part((p_mfgr)::text, '#'::text, 1) = 'Manufacturer'::text) AND (((split_part((p_mfgr)::text, '#'::text, 2))::integer >= 1) AND ((split_part((p_mfgr)::text, '#'::text, 2))::integer <= 5)) AND (array_length(string_to_array((p_mfgr)::text, '#'::text), 1) = 2))),\n" +
//							"    CONSTRAINT p_name_domain CHECK (((split_part((p_name)::text, ' '::text, 1) = ANY (ARRAY['almond'::text, 'antique'::text, 'aquamarine'::text, 'azure'::text, 'beige'::text, 'bisque'::text, 'black'::text, 'blanched'::text, 'blue'::text, 'blush'::text, 'brown'::text, 'burlywood'::text, 'burnished'::text, 'chartreuse'::text, 'chiffon'::text, 'chocolate'::text, 'coral'::text, 'cornflower'::text, 'cornsilk'::text, 'cream'::text, 'cyan'::text, 'dark'::text, 'deep'::text, 'dim'::text, 'dodger'::text, 'drab'::text, 'firebrick'::text, 'floral'::text, 'forest'::text, 'frosted'::text, 'gainsboro'::text, 'ghost'::text, 'goldenrod'::text, 'green'::text, 'grey'::text, 'honeydew'::text, 'hot'::text, 'indian'::text, 'ivory'::text, 'khaki'::text, 'lace'::text, 'lavender'::text, 'lawn'::text, 'lemon'::text, 'light'::text, 'lime'::text, 'linen'::text, 'magenta'::text, 'maroon'::text, 'medium'::text, 'metallic'::text, 'midnight'::text, 'mint'::text, 'misty'::text, 'moccasin'::text, 'navajo'::text, 'navy'::text, 'olive'::text, 'orange'::text, 'orchid'::text, 'pale'::text, 'papaya'::text, 'peach'::text, 'peru'::text, 'pink'::text, 'plum'::text, 'powder'::text, 'puff'::text, 'purple'::text, 'red'::text, 'rose'::text, 'rosy'::text, 'royal'::text, 'saddle'::text, 'salmon'::text, 'sandy'::text, 'seashell'::text, 'sienna'::text, 'sky'::text, 'slate'::text, 'smoke'::text, 'snow'::text, 'spring'::text, 'steel'::text, 'tan'::text, 'thistle'::text, 'tomato'::text, 'turquoise'::text, 'violet'::text, 'wheat'::text, 'white'::text, 'yellow'::text])) AND (split_part((p_name)::text, ' '::text, 2) = ANY (ARRAY['almond'::text, 'antique'::text, 'aquamarine'::text, 'azure'::text, 'beige'::text, 'bisque'::text, 'black'::text, 'blanched'::text, 'blue'::text, 'blush'::text, 'brown'::text, 'burlywood'::text, 'burnished'::text, 'chartreuse'::text, 'chiffon'::text, 'chocolate'::text, 'coral'::text, 'cornflower'::text, 'cornsilk'::text, 'cream'::text, 'cyan'::text, 'dark'::text, 'deep'::text, 'dim'::text, 'dodger'::text, 'drab'::text, 'firebrick'::text, 'floral'::text, 'forest'::text, 'frosted'::text, 'gainsboro'::text, 'ghost'::text, 'goldenrod'::text, 'green'::text, 'grey'::text, 'honeydew'::text, 'hot'::text, 'indian'::text, 'ivory'::text, 'khaki'::text, 'lace'::text, 'lavender'::text, 'lawn'::text, 'lemon'::text, 'light'::text, 'lime'::text, 'linen'::text, 'magenta'::text, 'maroon'::text, 'medium'::text, 'metallic'::text, 'midnight'::text, 'mint'::text, 'misty'::text, 'moccasin'::text, 'navajo'::text, 'navy'::text, 'olive'::text, 'orange'::text, 'orchid'::text, 'pale'::text, 'papaya'::text, 'peach'::text, 'peru'::text, 'pink'::text, 'plum'::text, 'powder'::text, 'puff'::text, 'purple'::text, 'red'::text, 'rose'::text, 'rosy'::text, 'royal'::text, 'saddle'::text, 'salmon'::text, 'sandy'::text, 'seashell'::text, 'sienna'::text, 'sky'::text, 'slate'::text, 'smoke'::text, 'snow'::text, 'spring'::text, 'steel'::text, 'tan'::text, 'thistle'::text, 'tomato'::text, 'turquoise'::text, 'violet'::text, 'wheat'::text, 'white'::text, 'yellow'::text])) AND (split_part((p_name)::text, ' '::text, 3) = ANY (ARRAY['almond'::text, 'antique'::text, 'aquamarine'::text, 'azure'::text, 'beige'::text, 'bisque'::text, 'black'::text, 'blanched'::text, 'blue'::text, 'blush'::text, 'brown'::text, 'burlywood'::text, 'burnished'::text, 'chartreuse'::text, 'chiffon'::text, 'chocolate'::text, 'coral'::text, 'cornflower'::text, 'cornsilk'::text, 'cream'::text, 'cyan'::text, 'dark'::text, 'deep'::text, 'dim'::text, 'dodger'::text, 'drab'::text, 'firebrick'::text, 'floral'::text, 'forest'::text, 'frosted'::text, 'gainsboro'::text, 'ghost'::text, 'goldenrod'::text, 'green'::text, 'grey'::text, 'honeydew'::text, 'hot'::text, 'indian'::text, 'ivory'::text, 'khaki'::text, 'lace'::text, 'lavender'::text, 'lawn'::text, 'lemon'::text, 'light'::text, 'lime'::text, 'linen'::text, 'magenta'::text, 'maroon'::text, 'medium'::text, 'metallic'::text, 'midnight'::text, 'mint'::text, 'misty'::text, 'moccasin'::text, 'navajo'::text, 'navy'::text, 'olive'::text, 'orange'::text, 'orchid'::text, 'pale'::text, 'papaya'::text, 'peach'::text, 'peru'::text, 'pink'::text, 'plum'::text, 'powder'::text, 'puff'::text, 'purple'::text, 'red'::text, 'rose'::text, 'rosy'::text, 'royal'::text, 'saddle'::text, 'salmon'::text, 'sandy'::text, 'seashell'::text, 'sienna'::text, 'sky'::text, 'slate'::text, 'smoke'::text, 'snow'::text, 'spring'::text, 'steel'::text, 'tan'::text, 'thistle'::text, 'tomato'::text, 'turquoise'::text, 'violet'::text, 'wheat'::text, 'white'::text, 'yellow'::text])) AND (split_part((p_name)::text, ' '::text, 4) = ANY (ARRAY['almond'::text, 'antique'::text, 'aquamarine'::text, 'azure'::text, 'beige'::text, 'bisque'::text, 'black'::text, 'blanched'::text, 'blue'::text, 'blush'::text, 'brown'::text, 'burlywood'::text, 'burnished'::text, 'chartreuse'::text, 'chiffon'::text, 'chocolate'::text, 'coral'::text, 'cornflower'::text, 'cornsilk'::text, 'cream'::text, 'cyan'::text, 'dark'::text, 'deep'::text, 'dim'::text, 'dodger'::text, 'drab'::text, 'firebrick'::text, 'floral'::text, 'forest'::text, 'frosted'::text, 'gainsboro'::text, 'ghost'::text, 'goldenrod'::text, 'green'::text, 'grey'::text, 'honeydew'::text, 'hot'::text, 'indian'::text, 'ivory'::text, 'khaki'::text, 'lace'::text, 'lavender'::text, 'lawn'::text, 'lemon'::text, 'light'::text, 'lime'::text, 'linen'::text, 'magenta'::text, 'maroon'::text, 'medium'::text, 'metallic'::text, 'midnight'::text, 'mint'::text, 'misty'::text, 'moccasin'::text, 'navajo'::text, 'navy'::text, 'olive'::text, 'orange'::text, 'orchid'::text, 'pale'::text, 'papaya'::text, 'peach'::text, 'peru'::text, 'pink'::text, 'plum'::text, 'powder'::text, 'puff'::text, 'purple'::text, 'red'::text, 'rose'::text, 'rosy'::text, 'royal'::text, 'saddle'::text, 'salmon'::text, 'sandy'::text, 'seashell'::text, 'sienna'::text, 'sky'::text, 'slate'::text, 'smoke'::text, 'snow'::text, 'spring'::text, 'steel'::text, 'tan'::text, 'thistle'::text, 'tomato'::text, 'turquoise'::text, 'violet'::text, 'wheat'::text, 'white'::text, 'yellow'::text])) AND (split_part((p_name)::text, ' '::text, 5) = ANY (ARRAY['almond'::text, 'antique'::text, 'aquamarine'::text, 'azure'::text, 'beige'::text, 'bisque'::text, 'black'::text, 'blanched'::text, 'blue'::text, 'blush'::text, 'brown'::text, 'burlywood'::text, 'burnished'::text, 'chartreuse'::text, 'chiffon'::text, 'chocolate'::text, 'coral'::text, 'cornflower'::text, 'cornsilk'::text, 'cream'::text, 'cyan'::text, 'dark'::text, 'deep'::text, 'dim'::text, 'dodger'::text, 'drab'::text, 'firebrick'::text, 'floral'::text, 'forest'::text, 'frosted'::text, 'gainsboro'::text, 'ghost'::text, 'goldenrod'::text, 'green'::text, 'grey'::text, 'honeydew'::text, 'hot'::text, 'indian'::text, 'ivory'::text, 'khaki'::text, 'lace'::text, 'lavender'::text, 'lawn'::text, 'lemon'::text, 'light'::text, 'lime'::text, 'linen'::text, 'magenta'::text, 'maroon'::text, 'medium'::text, 'metallic'::text, 'midnight'::text, 'mint'::text, 'misty'::text, 'moccasin'::text, 'navajo'::text, 'navy'::text, 'olive'::text, 'orange'::text, 'orchid'::text, 'pale'::text, 'papaya'::text, 'peach'::text, 'peru'::text, 'pink'::text, 'plum'::text, 'powder'::text, 'puff'::text, 'purple'::text, 'red'::text, 'rose'::text, 'rosy'::text, 'royal'::text, 'saddle'::text, 'salmon'::text, 'sandy'::text, 'seashell'::text, 'sienna'::text, 'sky'::text, 'slate'::text, 'smoke'::text, 'snow'::text, 'spring'::text, 'steel'::text, 'tan'::text, 'thistle'::text, 'tomato'::text, 'turquoise'::text, 'violet'::text, 'wheat'::text, 'white'::text, 'yellow'::text])) AND (array_length(string_to_array((p_name)::text, ' '::text), 1) = 5))),\n" +
							"    CONSTRAINT p_size_domain CHECK (((p_size >= 1) AND (p_size <= 50)))\n" + //",\n" +
//							"    CONSTRAINT p_type_domain CHECK (((split_part((p_type)::text, ' '::text, 1) = ANY (ARRAY['ECONOMY'::text, 'LARGE'::text, 'MEDIUM'::text, 'PROMO'::text, 'SMALL'::text, 'STANDARD'::text])) AND (split_part((p_type)::text, ' '::text, 2) = ANY (ARRAY['ANODIZED'::text, 'BRUSHED'::text, 'BURNISHED'::text, 'PLATED'::text, 'POLISHED'::text])) AND (split_part((p_type)::text, ' '::text, 3) = ANY (ARRAY['BRASS'::text, 'COPPER'::text, 'NICKEL'::text, 'STEEL'::text, 'TIN'::text])) AND (array_length(string_to_array((p_type)::text, ' '::text), 1) = 3)))\n" +
							")")

			.put("partsupp",
					"CREATE TABLE partsupp (\n" +
							"    ps_partkey integer NOT NULL,\n" +
							"    ps_suppkey integer NOT NULL,\n" +
							"    ps_availqty integer,\n" +
							"    ps_supplycost numeric,\n" +
							"    ps_comment character varying(199),\n" +
							"    CONSTRAINT ps_availqty_domain CHECK (((ps_availqty >= 1) AND (ps_availqty <= 9999))),\n" +
							"    CONSTRAINT ps_supplycost_domain CHECK (((ps_supplycost >= 1.00) AND (ps_supplycost <= 1000.00)))\n" +
							")")

			.put("region",
					"CREATE TABLE region (\n" +
							"    r_regionkey integer NOT NULL,\n" +
							"    r_name character(25),\n" +
							"    r_comment character varying(152),\n" +
							"    CONSTRAINT r_name_domain CHECK (r_name in ('MIDDLE EAST', 'AMERICA', 'ASIA', 'EUROPE', 'AFRICA')),\n" +
							"    CONSTRAINT r_regionkey_domain CHECK (((r_regionkey >= 0) AND (r_regionkey <= 4)))\n" +
//							"    CONSTRAINT r_regionkey_name_domain CHECK ((((r_regionkey = 0) AND (r_name = 'AFRICA')) OR ((r_regionkey = 1) AND (r_name = 'AMERICA')) OR ((r_regionkey = 2) AND (r_name = 'ASIA')) OR ((r_regionkey = 3) AND (r_name = 'EUROPE')) OR ((r_regionkey = 4) AND (r_name = 'MIDDLE EAST'))))\n" +
							")")

			.put("supplier",
					"CREATE TABLE supplier (\n" +
							"    s_suppkey integer NOT NULL,\n" +
							"    s_name character(25),\n" +
							"    s_address character varying(40),\n" +
							"    s_nationkey integer,\n" +
							"    s_phone character(15),\n" +
							"    s_acctbal numeric,\n" +
							"    s_comment character varying(101),\n" +
							"    CONSTRAINT s_acctbal_domain CHECK (((s_acctbal >= CAST('-999.99' AS numeric)) AND (s_acctbal <= 9999.99))),\n" +
//							"    CONSTRAINT s_name_domain CHECK (((split_part((s_name)::text, '#'::text, 1) = 'Supplier'::text) AND ((split_part((s_name)::text, '#'::text, 2))::integer = s_suppkey) AND (length(split_part((s_name)::text, '#'::text, 2)) = 9) AND (array_length(string_to_array((s_name)::text, '#'::text), 1) = 2))),\n" +
							"    CONSTRAINT s_nationkey_domain CHECK (((s_nationkey >= 0) AND (s_nationkey <= 24))),\n" +
//							"    CONSTRAINT s_phone_domain CHECK ((((\"substring\"((s_phone)::text, 1, 2))::integer = (s_nationkey + 10)) AND (\"substring\"((s_phone)::text, 3, 1) = '-'::text) AND (((\"substring\"((s_phone)::text, 4, 3))::integer >= 100) AND ((\"substring\"((s_phone)::text, 4, 3))::integer <= 999)) AND (\"substring\"((s_phone)::text, 7, 1) = '-'::text) AND (((\"substring\"((s_phone)::text, 8, 3))::integer >= 100) AND ((\"substring\"((s_phone)::text, 8, 3))::integer <= 999)) AND (\"substring\"((s_phone)::text, 11, 1) = '-'::text) AND (((\"substring\"((s_phone)::text, 12, 4))::integer >= 1000) AND ((\"substring\"((s_phone)::text, 12, 4))::integer <= 9999)))),\n" +
							"    CONSTRAINT s_suppkey_domain CHECK (((s_suppkey >= 1) AND (s_suppkey <= 1000)))\n" +
							")")

			.build();


	protected void setUp() throws Exception {
		SystemConfiguration.getInstance("tpch").closeCalciteConnection();
		SystemConfiguration.resetConfiguration();
		SystemCatalog.resetInstance();
		ConnectionManager.reset();

		config = SystemConfiguration.getInstance("tpch");
		catalog = SystemCatalog.getInstance();

		parser = new SqlStatementParser();
		honestBroker = SystemConfiguration.getInstance().getHonestBrokerConfig();
		logger = SystemConfiguration.getInstance().getLogger();
		dialect = config.DIALECT;

	}


	protected String readSQL(int queryNo) {
		return QUERIES.get(queryNo-1); // zero-indexed
	}

	@Override
	protected void tearDown() throws Exception {
		// clean up any dangling resources
		ConnectionManager connections = ConnectionManager.getInstance();
		if(connections != null) {
			connections.closeConnections();
			ConnectionManager.reset();
		}

		SystemConfiguration.getInstance().closeCalciteConnection();
		SystemConfiguration.resetConfiguration();
		SystemCatalog.resetInstance();

		// delete any generated classfiles
		String classFiles = Utilities.getVaultDBRoot() + "/target/classes/org/vaultdb/compiler/emp/generated/*.class";
		Utilities.runCmd("rm " + classFiles);
	}





}
