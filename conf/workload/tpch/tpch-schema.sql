--
-- PostgreSQL database dump
--

-- Dumped from database version 12.1
-- Dumped by pg_dump version 12.1

--
-- Name: customer; Type: TABLE; Schema: public; Owner: vaultdb
--

CREATE TABLE public.customer (
    c_custkey integer NOT NULL,
    c_name character varying(25),
    c_address character varying(40),
    c_nationkey integer,
    c_phone character(15),
    c_acctbal numeric,
    c_mktsegment character(10),
    c_comment character varying(117),
    CONSTRAINT c_acctbal_domain CHECK (c_acctbal >= CAST('-999.99' AS numeric) AND c_acctbal <= 9999.99),
    CONSTRAINT c_custkey_domain CHECK (((c_custkey >= 1) AND (c_custkey <= 15000))),
    CONSTRAINT c_mktsegment_domain CHECK (c_mktsegment IN ('MACHINERY', 'AUTOMOBILE', 'BUILDING', 'FURNITURE', 'HOUSEHOLD')),
    CONSTRAINT c_nationkey_domain CHECK (((c_nationkey >= 0) AND (c_nationkey <= 24)))
);
--
-- Name: lineitem; Type: TABLE; Schema: public; Owner: vaultdb
--

CREATE TABLE public.lineitem (
    l_orderkey integer NOT NULL,
    l_partkey integer,
    l_suppkey integer,
    l_linenumber integer NOT NULL,
    l_quantity numeric,
    l_extendedprice numeric,
    l_discount numeric,
    l_tax numeric,
    l_returnflag character(1),
    l_linestatus character(1),
    l_shipdate date,
    l_commitdate date,
    l_receiptdate date,
    l_shipinstruct character(25),
    l_shipmode character(10),
    l_comment character varying(44),
    CONSTRAINT l_discount_domain CHECK (((l_discount >= 0.00) AND (l_discount <= 0.10))),
    CONSTRAINT l_linenumber_domain CHECK (((l_linenumber >= 1) AND (l_linenumber <= 7))),
--    CONSTRAINT l_linestatus_based_on_shipdate_domain CHECK ((((l_linestatus = 'O'::bpchar) AND (l_shipdate > '1995-06-17'::date)) OR ((l_linestatus = 'F'::bpchar) AND (l_shipdate <= '1995-06-17'::date)))),
    CONSTRAINT l_linestatus_domain CHECK (l_linestatus IN ('O', 'F')),
    CONSTRAINT l_partkey_domain CHECK (((l_partkey >= 1) AND (l_partkey <= 20000))),
    CONSTRAINT l_quantity_domain CHECK ((l_quantity >= 1) AND (l_quantity <= 50)),
--    CONSTRAINT l_receiptdate_domain CHECK (((l_receiptdate >= (l_shipdate + 1)) AND (l_receiptdate <= (l_shipdate + 30)))),
--    CONSTRAINT l_returnflag_based_on_receiptdate_domain CHECK ((((l_returnflag = ANY (ARRAY['R'::bpchar, 'A'::bpchar])) AND (l_receiptdate <= '1995-06-17'::date)) OR ((l_returnflag = 'N'::bpchar) AND (l_receiptdate > '1995-06-17'::date)))),
    CONSTRAINT l_returnflag_domain CHECK (l_returnflag IN ('R', 'A', 'N')),
    CONSTRAINT l_shipinstruct_domain CHECK (l_shipinstruct IN ('DELIVER IN PERSON', 'COLLECT COD', 'NONE','TAKE BACK RETURN')),
    CONSTRAINT l_shipmode_domain CHECK (l_shipmode IN ('REG AIR', 'AIR', 'RAIL', 'SHIP', 'TRUCK', 'MAIL', 'FOB')),
    CONSTRAINT l_tax_domain CHECK (((l_tax >= 0.00) AND (l_tax <= 0.08)))
);


CREATE TABLE public.nation (
    n_nationkey integer NOT NULL,
    n_name character(25),
    n_regionkey integer,
    n_comment character varying(152),
--    CONSTRAINT n_name_domain CHECK (n_name IN ('ETHIOPIA', 'IRAN', 'EGYPT', 'RUSSIA', 'SAUDI ARABIA', 'INDONESIA', 'VIETNAM', 'GERMANY', 'PERU', 'FRANCE', 'ALGERIA', 'ROMANIA', 'JORDAN', 'ARGENTINA', 'MOROCCO', 'CANADA', 'JAPAN', 'INDIA', 'UNITED KINGDOM', 'UNITED STATES', 'MOZAMBIQUE', 'CHINA', 'BRAZIL', 'KENYA', 'IRAQ')),
    CONSTRAINT n_nationkey_domain CHECK (((n_nationkey >= 0) AND (n_nationkey <= 24))),
--    CONSTRAINT n_nationkey_name_regionkey_domain CHECK ((((n_nationkey = 0) AND (n_name = 'ALGERIA'::bpchar) AND (n_regionkey = 0)) OR ((n_nationkey = 1) AND (n_name = 'ARGENTINA'::bpchar) AND (n_regionkey = 1)) OR ((n_nationkey = 2) AND (n_name = 'BRAZIL'::bpchar) AND (n_regionkey = 1)) OR ((n_nationkey = 3) AND (n_name = 'CANADA'::bpchar) AND (n_regionkey = 1)) OR ((n_nationkey = 4) AND (n_name = 'EGYPT'::bpchar) AND (n_regionkey = 4)) OR ((n_nationkey = 5) AND (n_name = 'ETHIOPIA'::bpchar) AND (n_regionkey = 0)) OR ((n_nationkey = 6) AND (n_name = 'FRANCE'::bpchar) AND (n_regionkey = 3)) OR ((n_nationkey = 7) AND (n_name = 'GERMANY'::bpchar) AND (n_regionkey = 3)) OR ((n_nationkey = 8) AND (n_name = 'INDIA'::bpchar) AND (n_regionkey = 2)) OR ((n_nationkey = 9) AND (n_name = 'INDONESIA'::bpchar) AND (n_regionkey = 2)) OR ((n_nationkey = 10) AND (n_name = 'IRAN'::bpchar) AND (n_regionkey = 4)) OR ((n_nationkey = 11) AND (n_name = 'IRAQ'::bpchar) AND (n_regionkey = 4)) OR ((n_nationkey = 12) AND (n_name = 'JAPAN'::bpchar) AND (n_regionkey = 2)) OR ((n_nationkey = 13) AND (n_name = 'JORDAN'::bpchar) AND (n_regionkey = 4)) OR ((n_nationkey = 14) AND (n_name = 'KENYA'::bpchar) AND (n_regionkey = 0)) OR ((n_nationkey = 15) AND (n_name = 'MOROCCO'::bpchar) AND (n_regionkey = 0)) OR ((n_nationkey = 16) AND (n_name = 'MOZAMBIQUE'::bpchar) AND (n_regionkey = 0)) OR ((n_nationkey = 17) AND (n_name = 'PERU'::bpchar) AND (n_regionkey = 1)) OR ((n_nationkey = 18) AND (n_name = 'CHINA'::bpchar) AND (n_regionkey = 2)) OR ((n_nationkey = 19) AND (n_name = 'ROMANIA'::bpchar) AND (n_regionkey = 3)) OR ((n_nationkey = 20) AND (n_name = 'SAUDI ARABIA'::bpchar) AND (n_regionkey = 4)) OR ((n_nationkey = 21) AND (n_name = 'VIETNAM'::bpchar) AND (n_regionkey = 2)) OR ((n_nationkey = 22) AND (n_name = 'RUSSIA'::bpchar) AND (n_regionkey = 3)) OR ((n_nationkey = 23) AND (n_name = 'UNITED KINGDOM'::bpchar) AND (n_regionkey = 3)) OR ((n_nationkey = 24) AND (n_name = 'UNITED STATES'::bpchar) AND (n_regionkey = 1)))),
    CONSTRAINT n_regionkey_domain CHECK (((n_regionkey >= 0) AND (n_regionkey <= 4)))
);


--
-- Name: orders; Type: TABLE; Schema: public; Owner: vaultdb
--

CREATE TABLE public.orders (
    o_orderkey integer NOT NULL,
    o_custkey integer,
    o_orderstatus character(1),
    o_totalprice numeric,
    o_orderdate date,
    o_orderyear integer,
    o_orderpriority character(15),
    o_clerk character(15),
    o_shippriority integer,
    o_comment character varying(79),
--    CONSTRAINT o_clerk_domain CHECK (((split_part((o_clerk)::text, '#'::text, 1) = 'Clerk'::text) AND (((split_part((o_clerk)::text, '#'::text, 2))::integer >= 1) AND ((split_part((o_clerk)::text, '#'::text, 2))::integer <= 1000)) AND (length(split_part((o_clerk)::text, '#'::text, 2)) = 9) AND (array_length(string_to_array((o_clerk)::text, '#'::text), 1) = 2))),
    CONSTRAINT o_orderdate_domain CHECK ((o_orderdate >= CAST('1992-01-01' AS date)) AND (o_orderdate <= CAST('1998-12-31' AS date))),
    CONSTRAINT o_orderyear_domain CHECK (((o_orderyear >= 1992) AND (o_orderyear <= 1998))),
    CONSTRAINT o_orderkey_domain CHECK (((o_orderkey >= 1) AND (o_orderkey <= 600000))),
    CONSTRAINT o_orderpriority_domain CHECK ((o_orderpriority IN ('1-URGENT', '2-HIGH', '3-MEDIUM', '4-NOT SPECIFIED', '5-LOW'))),
    CONSTRAINT o_orderstatus_domain CHECK ((o_orderstatus IN ('F', 'O', 'P'))),
    CONSTRAINT o_shippriority_domain CHECK ((o_shippriority = 0))
);


--
-- Name: part; Type: TABLE; Schema: public; Owner: vaultdb
--

CREATE TABLE public.part (
    p_partkey integer NOT NULL,
    p_name character varying(55),
    p_mfgr character(25),
    p_brand character(10),
    p_type character varying(25),
    p_size integer,
    p_container character(10),
    p_retailprice numeric,
    p_comment character varying(23),
-- constraints not needed, will infer automatically for public attribute
--    CONSTRAINT p_container_domain CHECK (p_container IN (('JUMBO BAG', 'JUMBO BOX', 'JUMBO CAN', 'JUMBO CASE', 'JUMBO DRUM', 'JUMBO JAR', 'JUMBO PACK', 'JUMBO PKG', 'LG BAG', 'LG BOX', 'LG CAN', 'LG CASE', 'LG DRUM', 'LG JAR', 'LG PACK', 'LG PKG', 'MED BAG', 'MED BOX', 'MED CAN', 'MED CASE', 'MED DRUM', 'MED JAR', 'MED PACK', 'MED PKG', 'SM BAG', 'SM BOX', 'SM CAN', 'SM CASE', 'SM DRUM', 'SM JAR', 'SM PACK', 'SM PKG'))),
--    CONSTRAINT p_brand_domain CHECK (((split_part((p_brand)::text, '#'::text, 1) = 'Brand'::text) AND (("left"(split_part((p_brand)::text, '#'::text, 2), 1))::integer = (split_part((p_mfgr)::text, '#'::text, 2))::integer) AND ((("right"(split_part((p_brand)::text, '#'::text, 2), 1))::integer >= 1) AND (("right"(split_part((p_brand)::text, '#'::text, 2), 1))::integer <= 5)) AND (length(split_part((p_brand)::text, '#'::text, 2)) = 2) AND (array_length(string_to_array((p_brand)::text, '#'::text), 1) = 2))),
--    CONSTRAINT p_container_domain CHECK (((split_part((p_container)::text, ' '::text, 1) = ANY (ARRAY['JUMBO'::text, 'LG'::text, 'MED'::text, 'SM'::text, 'WRAP'::text])) AND (split_part((p_container)::text, ' '::text, 2) = ANY (ARRAY['BAG'::text, 'BOX'::text, 'CAN'::text, 'CASE'::text, 'DRUM'::text, 'JAR'::text, 'PACK'::text, 'PKG'::text])) AND (array_length(string_to_array((p_container)::text, ' '::text), 1) = 2))),
--    CONSTRAINT p_mfgr_domain CHECK (((split_part((p_mfgr)::text, '#'::text, 1) = 'Manufacturer'::text) AND (((split_part((p_mfgr)::text, '#'::text, 2))::integer >= 1) AND ((split_part((p_mfgr)::text, '#'::text, 2))::integer <= 5)) AND (array_length(string_to_array((p_mfgr)::text, '#'::text), 1) = 2))),
--    CONSTRAINT p_name_domain CHECK (((split_part((p_name)::text, ' '::text, 1) = ANY (ARRAY['almond'::text, 'antique'::text, 'aquamarine'::text, 'azure'::text, 'beige'::text, 'bisque'::text, 'black'::text, 'blanched'::text, 'blue'::text, 'blush'::text, 'brown'::text, 'burlywood'::text, 'burnished'::text, 'chartreuse'::text, 'chiffon'::text, 'chocolate'::text, 'coral'::text, 'cornflower'::text, 'cornsilk'::text, 'cream'::text, 'cyan'::text, 'dark'::text, 'deep'::text, 'dim'::text, 'dodger'::text, 'drab'::text, 'firebrick'::text, 'floral'::text, 'forest'::text, 'frosted'::text, 'gainsboro'::text, 'ghost'::text, 'goldenrod'::text, 'green'::text, 'grey'::text, 'honeydew'::text, 'hot'::text, 'indian'::text, 'ivory'::text, 'khaki'::text, 'lace'::text, 'lavender'::text, 'lawn'::text, 'lemon'::text, 'light'::text, 'lime'::text, 'linen'::text, 'magenta'::text, 'maroon'::text, 'medium'::text, 'metallic'::text, 'midnight'::text, 'mint'::text, 'misty'::text, 'moccasin'::text, 'navajo'::text, 'navy'::text, 'olive'::text, 'orange'::text, 'orchid'::text, 'pale'::text, 'papaya'::text, 'peach'::text, 'peru'::text, 'pink'::text, 'plum'::text, 'powder'::text, 'puff'::text, 'purple'::text, 'red'::text, 'rose'::text, 'rosy'::text, 'royal'::text, 'saddle'::text, 'salmon'::text, 'sandy'::text, 'seashell'::text, 'sienna'::text, 'sky'::text, 'slate'::text, 'smoke'::text, 'snow'::text, 'spring'::text, 'steel'::text, 'tan'::text, 'thistle'::text, 'tomato'::text, 'turquoise'::text, 'violet'::text, 'wheat'::text, 'white'::text, 'yellow'::text])) AND (split_part((p_name)::text, ' '::text, 2) = ANY (ARRAY['almond'::text, 'antique'::text, 'aquamarine'::text, 'azure'::text, 'beige'::text, 'bisque'::text, 'black'::text, 'blanched'::text, 'blue'::text, 'blush'::text, 'brown'::text, 'burlywood'::text, 'burnished'::text, 'chartreuse'::text, 'chiffon'::text, 'chocolate'::text, 'coral'::text, 'cornflower'::text, 'cornsilk'::text, 'cream'::text, 'cyan'::text, 'dark'::text, 'deep'::text, 'dim'::text, 'dodger'::text, 'drab'::text, 'firebrick'::text, 'floral'::text, 'forest'::text, 'frosted'::text, 'gainsboro'::text, 'ghost'::text, 'goldenrod'::text, 'green'::text, 'grey'::text, 'honeydew'::text, 'hot'::text, 'indian'::text, 'ivory'::text, 'khaki'::text, 'lace'::text, 'lavender'::text, 'lawn'::text, 'lemon'::text, 'light'::text, 'lime'::text, 'linen'::text, 'magenta'::text, 'maroon'::text, 'medium'::text, 'metallic'::text, 'midnight'::text, 'mint'::text, 'misty'::text, 'moccasin'::text, 'navajo'::text, 'navy'::text, 'olive'::text, 'orange'::text, 'orchid'::text, 'pale'::text, 'papaya'::text, 'peach'::text, 'peru'::text, 'pink'::text, 'plum'::text, 'powder'::text, 'puff'::text, 'purple'::text, 'red'::text, 'rose'::text, 'rosy'::text, 'royal'::text, 'saddle'::text, 'salmon'::text, 'sandy'::text, 'seashell'::text, 'sienna'::text, 'sky'::text, 'slate'::text, 'smoke'::text, 'snow'::text, 'spring'::text, 'steel'::text, 'tan'::text, 'thistle'::text, 'tomato'::text, 'turquoise'::text, 'violet'::text, 'wheat'::text, 'white'::text, 'yellow'::text])) AND (split_part((p_name)::text, ' '::text, 3) = ANY (ARRAY['almond'::text, 'antique'::text, 'aquamarine'::text, 'azure'::text, 'beige'::text, 'bisque'::text, 'black'::text, 'blanched'::text, 'blue'::text, 'blush'::text, 'brown'::text, 'burlywood'::text, 'burnished'::text, 'chartreuse'::text, 'chiffon'::text, 'chocolate'::text, 'coral'::text, 'cornflower'::text, 'cornsilk'::text, 'cream'::text, 'cyan'::text, 'dark'::text, 'deep'::text, 'dim'::text, 'dodger'::text, 'drab'::text, 'firebrick'::text, 'floral'::text, 'forest'::text, 'frosted'::text, 'gainsboro'::text, 'ghost'::text, 'goldenrod'::text, 'green'::text, 'grey'::text, 'honeydew'::text, 'hot'::text, 'indian'::text, 'ivory'::text, 'khaki'::text, 'lace'::text, 'lavender'::text, 'lawn'::text, 'lemon'::text, 'light'::text, 'lime'::text, 'linen'::text, 'magenta'::text, 'maroon'::text, 'medium'::text, 'metallic'::text, 'midnight'::text, 'mint'::text, 'misty'::text, 'moccasin'::text, 'navajo'::text, 'navy'::text, 'olive'::text, 'orange'::text, 'orchid'::text, 'pale'::text, 'papaya'::text, 'peach'::text, 'peru'::text, 'pink'::text, 'plum'::text, 'powder'::text, 'puff'::text, 'purple'::text, 'red'::text, 'rose'::text, 'rosy'::text, 'royal'::text, 'saddle'::text, 'salmon'::text, 'sandy'::text, 'seashell'::text, 'sienna'::text, 'sky'::text, 'slate'::text, 'smoke'::text, 'snow'::text, 'spring'::text, 'steel'::text, 'tan'::text, 'thistle'::text, 'tomato'::text, 'turquoise'::text, 'violet'::text, 'wheat'::text, 'white'::text, 'yellow'::text])) AND (split_part((p_name)::text, ' '::text, 4) = ANY (ARRAY['almond'::text, 'antique'::text, 'aquamarine'::text, 'azure'::text, 'beige'::text, 'bisque'::text, 'black'::text, 'blanched'::text, 'blue'::text, 'blush'::text, 'brown'::text, 'burlywood'::text, 'burnished'::text, 'chartreuse'::text, 'chiffon'::text, 'chocolate'::text, 'coral'::text, 'cornflower'::text, 'cornsilk'::text, 'cream'::text, 'cyan'::text, 'dark'::text, 'deep'::text, 'dim'::text, 'dodger'::text, 'drab'::text, 'firebrick'::text, 'floral'::text, 'forest'::text, 'frosted'::text, 'gainsboro'::text, 'ghost'::text, 'goldenrod'::text, 'green'::text, 'grey'::text, 'honeydew'::text, 'hot'::text, 'indian'::text, 'ivory'::text, 'khaki'::text, 'lace'::text, 'lavender'::text, 'lawn'::text, 'lemon'::text, 'light'::text, 'lime'::text, 'linen'::text, 'magenta'::text, 'maroon'::text, 'medium'::text, 'metallic'::text, 'midnight'::text, 'mint'::text, 'misty'::text, 'moccasin'::text, 'navajo'::text, 'navy'::text, 'olive'::text, 'orange'::text, 'orchid'::text, 'pale'::text, 'papaya'::text, 'peach'::text, 'peru'::text, 'pink'::text, 'plum'::text, 'powder'::text, 'puff'::text, 'purple'::text, 'red'::text, 'rose'::text, 'rosy'::text, 'royal'::text, 'saddle'::text, 'salmon'::text, 'sandy'::text, 'seashell'::text, 'sienna'::text, 'sky'::text, 'slate'::text, 'smoke'::text, 'snow'::text, 'spring'::text, 'steel'::text, 'tan'::text, 'thistle'::text, 'tomato'::text, 'turquoise'::text, 'violet'::text, 'wheat'::text, 'white'::text, 'yellow'::text])) AND (split_part((p_name)::text, ' '::text, 5) = ANY (ARRAY['almond'::text, 'antique'::text, 'aquamarine'::text, 'azure'::text, 'beige'::text, 'bisque'::text, 'black'::text, 'blanched'::text, 'blue'::text, 'blush'::text, 'brown'::text, 'burlywood'::text, 'burnished'::text, 'chartreuse'::text, 'chiffon'::text, 'chocolate'::text, 'coral'::text, 'cornflower'::text, 'cornsilk'::text, 'cream'::text, 'cyan'::text, 'dark'::text, 'deep'::text, 'dim'::text, 'dodger'::text, 'drab'::text, 'firebrick'::text, 'floral'::text, 'forest'::text, 'frosted'::text, 'gainsboro'::text, 'ghost'::text, 'goldenrod'::text, 'green'::text, 'grey'::text, 'honeydew'::text, 'hot'::text, 'indian'::text, 'ivory'::text, 'khaki'::text, 'lace'::text, 'lavender'::text, 'lawn'::text, 'lemon'::text, 'light'::text, 'lime'::text, 'linen'::text, 'magenta'::text, 'maroon'::text, 'medium'::text, 'metallic'::text, 'midnight'::text, 'mint'::text, 'misty'::text, 'moccasin'::text, 'navajo'::text, 'navy'::text, 'olive'::text, 'orange'::text, 'orchid'::text, 'pale'::text, 'papaya'::text, 'peach'::text, 'peru'::text, 'pink'::text, 'plum'::text, 'powder'::text, 'puff'::text, 'purple'::text, 'red'::text, 'rose'::text, 'rosy'::text, 'royal'::text, 'saddle'::text, 'salmon'::text, 'sandy'::text, 'seashell'::text, 'sienna'::text, 'sky'::text, 'slate'::text, 'smoke'::text, 'snow'::text, 'spring'::text, 'steel'::text, 'tan'::text, 'thistle'::text, 'tomato'::text, 'turquoise'::text, 'violet'::text, 'wheat'::text, 'white'::text, 'yellow'::text])) AND (array_length(string_to_array((p_name)::text, ' '::text), 1) = 5))),
    CONSTRAINT p_size_domain CHECK (((p_size >= 1) AND (p_size <= 50)))
--    CONSTRAINT p_type_domain CHECK (((split_part((p_type)::text, ' '::text, 1) = ANY (ARRAY['ECONOMY'::text, 'LARGE'::text, 'MEDIUM'::text, 'PROMO'::text, 'SMALL'::text, 'STANDARD'::text])) AND (split_part((p_type)::text, ' '::text, 2) = ANY (ARRAY['ANODIZED'::text, 'BRUSHED'::text, 'BURNISHED'::text, 'PLATED'::text, 'POLISHED'::text])) AND (split_part((p_type)::text, ' '::text, 3) = ANY (ARRAY['BRASS'::text, 'COPPER'::text, 'NICKEL'::text, 'STEEL'::text, 'TIN'::text])) AND (array_length(string_to_array((p_type)::text, ' '::text), 1) = 3)))
);

CREATE TABLE public.partsupp (
    ps_partkey integer NOT NULL,
    ps_suppkey integer NOT NULL,
    ps_availqty integer,
    ps_supplycost numeric,
    ps_comment character varying(199),
    CONSTRAINT ps_availqty_domain CHECK (((ps_availqty >= 1) AND (ps_availqty <= 9999))),
    CONSTRAINT ps_supplycost_domain CHECK (((ps_supplycost >= 1.00) AND (ps_supplycost <= 1000.00)))
);



--
-- Name: region; Type: TABLE; Schema: public; Owner: vaultdb
--

CREATE TABLE public.region (
    r_regionkey integer NOT NULL,
    r_name character(25),
    r_comment character varying(152),
    CONSTRAINT r_name_domain CHECK (r_name in ('MIDDLE EAST', 'AMERICA', 'ASIA', 'EUROPE', 'AFRICA')),
    CONSTRAINT r_regionkey_domain CHECK (((r_regionkey >= 0) AND (r_regionkey <= 4)))
--    CONSTRAINT r_regionkey_name_domain CHECK ((((r_regionkey = 0) AND (r_name = 'AFRICA'::bpchar)) OR ((r_regionkey = 1) AND (r_name = 'AMERICA'::bpchar)) OR ((r_regionkey = 2) AND (r_name = 'ASIA'::bpchar)) OR ((r_regionkey = 3) AND (r_name = 'EUROPE'::bpchar)) OR ((r_regionkey = 4) AND (r_name = 'MIDDLE EAST'::bpchar))))
);

--
-- Name: supplier; Type: TABLE; Schema: public; Owner: vaultdb
--

CREATE TABLE public.supplier (
    s_suppkey integer NOT NULL,
    s_name character(25),
    s_address character varying(40),
    s_nationkey integer,
    s_phone character(15),
    s_acctbal numeric,
    s_comment character varying(101),
     CONSTRAINT s_acctbal_domain CHECK (((s_acctbal >= CAST('-999.99' AS numeric)) AND (s_acctbal <= 9999.99))),
--    CONSTRAINT s_name_domain CHECK (((split_part((s_name)::text, '#'::text, 1) = 'Supplier'::text) AND ((split_part((s_name)::text, '#'::text, 2))::integer = s_suppkey) AND (length(split_part((s_name)::text, '#'::text, 2)) = 9) AND (array_length(string_to_array((s_name)::text, '#'::text), 1) = 2))),
    CONSTRAINT s_nationkey_domain CHECK (((s_nationkey >= 0) AND (s_nationkey <= 24))),
--    CONSTRAINT s_phone_domain CHECK (((("substring"((s_phone)::text, 1, 2))::integer = (s_nationkey + 10)) AND ("substring"((s_phone)::text, 3, 1) = '-'::text) AND ((("substring"((s_phone)::text, 4, 3))::integer >= 100) AND (("substring"((s_phone)::text, 4, 3))::integer <= 999)) AND ("substring"((s_phone)::text, 7, 1) = '-'::text) AND ((("substring"((s_phone)::text, 8, 3))::integer >= 100) AND (("substring"((s_phone)::text, 8, 3))::integer <= 999)) AND ("substring"((s_phone)::text, 11, 1) = '-'::text) AND ((("substring"((s_phone)::text, 12, 4))::integer >= 1000) AND (("substring"((s_phone)::text, 12, 4))::integer <= 9999)))),
    CONSTRAINT s_suppkey_domain CHECK (((s_suppkey >= 1) AND (s_suppkey <= 1000)))
);
