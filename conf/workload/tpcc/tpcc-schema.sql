--
-- PostgreSQL database dump
--

-- Dumped from database version 13.7 (Ubuntu 13.7-0ubuntu0.21.10.1)
-- Dumped by pg_dump version 13.7 (Ubuntu 13.7-0ubuntu0.21.10.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: customer; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.customer (
    c_w_id integer NOT NULL,
    c_d_id integer NOT NULL,
    c_id integer NOT NULL,
    c_discount numeric(4,4) NOT NULL,
    c_credit character(2) NOT NULL,
    c_last character varying(16) NOT NULL,
    c_first character varying(16) NOT NULL,
    c_credit_lim numeric(12,2) NOT NULL,
    c_balance numeric(12,2) NOT NULL,
    c_ytd_payment double precision NOT NULL,
    c_payment_cnt integer NOT NULL,
    c_delivery_cnt integer NOT NULL,
    c_street_1 character varying(20) NOT NULL,
    c_street_2 character varying(20) NOT NULL,
    c_city character varying(20) NOT NULL,
    c_state character(2) NOT NULL,
    c_zip character(9) NOT NULL,
    c_phone character(16) NOT NULL,
    c_since timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    c_middle character(2) NOT NULL,
    c_data character varying(500) NOT NULL
);


ALTER TABLE public.customer OWNER TO zheng;

--
-- Name: district; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.district (
    d_w_id integer NOT NULL,
    d_id integer NOT NULL,
    d_ytd numeric(12,2) NOT NULL,
    d_tax numeric(4,4) NOT NULL,
    d_next_o_id integer NOT NULL,
    d_name character varying(10) NOT NULL,
    d_street_1 character varying(20) NOT NULL,
    d_street_2 character varying(20) NOT NULL,
    d_city character varying(20) NOT NULL,
    d_state character(2) NOT NULL,
    d_zip character(9) NOT NULL
);


ALTER TABLE public.district OWNER TO zheng;

--
-- Name: history; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.history (
    h_c_id integer NOT NULL,
    h_c_d_id integer NOT NULL,
    h_c_w_id integer NOT NULL,
    h_d_id integer NOT NULL,
    h_w_id integer NOT NULL,
    h_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    h_amount numeric(6,2) NOT NULL,
    h_data character varying(24) NOT NULL
);


ALTER TABLE public.history OWNER TO zheng;

--
-- Name: item; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.item (
    i_id integer NOT NULL,
    i_name character varying(24) NOT NULL,
    i_price numeric(5,2) NOT NULL,
    i_data character varying(50) NOT NULL,
    i_im_id integer NOT NULL
);


ALTER TABLE public.item OWNER TO zheng;

--
-- Name: lineitem; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.lineitem (
    l_orderkey integer NOT NULL,
    l_partkey integer NOT NULL,
    l_suppkey integer NOT NULL,
    l_linenumber integer NOT NULL,
    l_quantity numeric(15,2) NOT NULL,
    l_extendedprice numeric(15,2) NOT NULL,
    l_discount numeric(15,2) NOT NULL,
    l_tax numeric(15,2) NOT NULL,
    l_returnflag character(1) NOT NULL,
    l_linestatus character(1) NOT NULL,
    l_shipdate date NOT NULL,
    l_commitdate date NOT NULL,
    l_receiptdate date NOT NULL,
    l_shipinstruct character(25) NOT NULL,
    l_shipmode character(10) NOT NULL,
    l_comment character varying(44) NOT NULL
);


ALTER TABLE public.lineitem OWNER TO zheng;

--
-- Name: nation; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.nation (
    n_nationkey integer NOT NULL,
    n_name character(25) NOT NULL,
    n_regionkey integer NOT NULL,
    n_comment character varying(152)
);


ALTER TABLE public.nation OWNER TO zheng;

--
-- Name: new_order; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.new_order (
    no_w_id integer NOT NULL,
    no_d_id integer NOT NULL,
    no_o_id integer NOT NULL
);


ALTER TABLE public.new_order OWNER TO zheng;

--
-- Name: oorder; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.oorder (
    o_w_id integer NOT NULL,
    o_d_id integer NOT NULL,
    o_id integer NOT NULL,
    o_c_id integer NOT NULL,
    o_carrier_id integer,
    o_ol_cnt integer NOT NULL,
    o_all_local integer NOT NULL,
    o_entry_d timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.oorder OWNER TO zheng;

--
-- Name: order_line; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.order_line (
    ol_w_id integer NOT NULL,
    ol_d_id integer NOT NULL,
    ol_o_id integer NOT NULL,
    ol_number integer NOT NULL,
    ol_i_id integer NOT NULL,
    ol_delivery_d timestamp without time zone,
    ol_amount numeric(6,2) NOT NULL,
    ol_supply_w_id integer NOT NULL,
    ol_quantity numeric(6,2) NOT NULL,
    ol_dist_info character(24) NOT NULL
);


ALTER TABLE public.order_line OWNER TO zheng;

--
-- Name: orders; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.orders (
    o_orderkey integer NOT NULL,
    o_custkey integer NOT NULL,
    o_orderstatus character(1) NOT NULL,
    o_totalprice numeric(15,2) NOT NULL,
    o_orderdate date NOT NULL,
    o_orderpriority character(15) NOT NULL,
    o_clerk character(15) NOT NULL,
    o_shippriority integer NOT NULL,
    o_comment character varying(79) NOT NULL
);


ALTER TABLE public.orders OWNER TO zheng;

--
-- Name: part; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.part (
    p_partkey integer NOT NULL,
    p_name character varying(55) NOT NULL,
    p_mfgr character(25) NOT NULL,
    p_brand character(10) NOT NULL,
    p_type character varying(25) NOT NULL,
    p_size integer NOT NULL,
    p_container character(10) NOT NULL,
    p_retailprice numeric(15,2) NOT NULL,
    p_comment character varying(23) NOT NULL
);


ALTER TABLE public.part OWNER TO zheng;

--
-- Name: partsupp; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.partsupp (
    ps_partkey integer NOT NULL,
    ps_suppkey integer NOT NULL,
    ps_availqty integer NOT NULL,
    ps_supplycost numeric(15,2) NOT NULL,
    ps_comment character varying(199) NOT NULL
);


ALTER TABLE public.partsupp OWNER TO zheng;

--
-- Name: region; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.region (
    r_regionkey integer NOT NULL,
    r_name character(25) NOT NULL,
    r_comment character varying(152)
);


ALTER TABLE public.region OWNER TO zheng;

--
-- Name: stock; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.stock (
    s_w_id integer NOT NULL,
    s_i_id integer NOT NULL,
    s_quantity integer NOT NULL,
    s_ytd numeric(8,2) NOT NULL,
    s_order_cnt integer NOT NULL,
    s_remote_cnt integer NOT NULL,
    s_data character varying(50) NOT NULL,
    s_dist_01 character(24) NOT NULL,
    s_dist_02 character(24) NOT NULL,
    s_dist_03 character(24) NOT NULL,
    s_dist_04 character(24) NOT NULL,
    s_dist_05 character(24) NOT NULL,
    s_dist_06 character(24) NOT NULL,
    s_dist_07 character(24) NOT NULL,
    s_dist_08 character(24) NOT NULL,
    s_dist_09 character(24) NOT NULL,
    s_dist_10 character(24) NOT NULL
);


ALTER TABLE public.stock OWNER TO zheng;

--
-- Name: supplier; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.supplier (
    s_suppkey integer NOT NULL,
    s_name character(25) NOT NULL,
    s_address character varying(40) NOT NULL,
    s_nationkey integer NOT NULL,
    s_phone character(15) NOT NULL,
    s_acctbal numeric(15,2) NOT NULL,
    s_comment character varying(101) NOT NULL
);


ALTER TABLE public.supplier OWNER TO zheng;

--
-- Name: warehouse; Type: TABLE; Schema: public; Owner: zheng
--

CREATE TABLE public.warehouse (
    w_id integer NOT NULL,
    w_ytd numeric(12,2) NOT NULL,
    w_tax numeric(4,4) NOT NULL,
    w_name character varying(10) NOT NULL,
    w_street_1 character varying(20) NOT NULL,
    w_street_2 character varying(20) NOT NULL,
    w_city character varying(20) NOT NULL,
    w_state character(2) NOT NULL,
    w_zip character(9) NOT NULL
);


ALTER TABLE public.warehouse OWNER TO zheng;

--
-- Name: customer customer_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (c_w_id, c_d_id, c_id);


--
-- Name: district district_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.district
    ADD CONSTRAINT district_pkey PRIMARY KEY (d_w_id, d_id);


--
-- Name: item item_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT item_pkey PRIMARY KEY (i_id);


--
-- Name: lineitem lineitem_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.lineitem
    ADD CONSTRAINT lineitem_pkey PRIMARY KEY (l_orderkey, l_linenumber);


--
-- Name: nation nation_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.nation
    ADD CONSTRAINT nation_pkey PRIMARY KEY (n_nationkey);


--
-- Name: new_order new_order_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.new_order
    ADD CONSTRAINT new_order_pkey PRIMARY KEY (no_w_id, no_d_id, no_o_id);


--
-- Name: oorder oorder_o_w_id_o_d_id_o_c_id_o_id_key; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.oorder
    ADD CONSTRAINT oorder_o_w_id_o_d_id_o_c_id_o_id_key UNIQUE (o_w_id, o_d_id, o_c_id, o_id);


--
-- Name: oorder oorder_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.oorder
    ADD CONSTRAINT oorder_pkey PRIMARY KEY (o_w_id, o_d_id, o_id);


--
-- Name: order_line order_line_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.order_line
    ADD CONSTRAINT order_line_pkey PRIMARY KEY (ol_w_id, ol_d_id, ol_o_id, ol_number);


--
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (o_orderkey);


--
-- Name: part part_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.part
    ADD CONSTRAINT part_pkey PRIMARY KEY (p_partkey);


--
-- Name: partsupp partsupp_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.partsupp
    ADD CONSTRAINT partsupp_pkey PRIMARY KEY (ps_partkey, ps_suppkey);


--
-- Name: region region_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.region
    ADD CONSTRAINT region_pkey PRIMARY KEY (r_regionkey);


--
-- Name: stock stock_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT stock_pkey PRIMARY KEY (s_w_id, s_i_id);


--
-- Name: supplier supplier_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT supplier_pkey PRIMARY KEY (s_suppkey);


--
-- Name: warehouse warehouse_pkey; Type: CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.warehouse
    ADD CONSTRAINT warehouse_pkey PRIMARY KEY (w_id);


--
-- Name: idx_customer_name; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX idx_customer_name ON public.customer USING btree (c_w_id, c_d_id, c_last, c_first);


--
-- Name: idx_order; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX idx_order ON public.oorder USING btree (o_w_id, o_d_id, o_c_id, o_id);


--
-- Name: l_cd; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX l_cd ON public.lineitem USING btree (l_commitdate);


--
-- Name: l_ok; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX l_ok ON public.lineitem USING btree (l_orderkey);


--
-- Name: l_pk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX l_pk ON public.lineitem USING btree (l_partkey);


--
-- Name: l_pk_sk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX l_pk_sk ON public.lineitem USING btree (l_partkey, l_suppkey);


--
-- Name: l_rd; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX l_rd ON public.lineitem USING btree (l_receiptdate);


--
-- Name: l_sd; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX l_sd ON public.lineitem USING btree (l_shipdate);


--
-- Name: l_sk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX l_sk ON public.lineitem USING btree (l_suppkey);


--
-- Name: l_sk_pk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX l_sk_pk ON public.lineitem USING btree (l_suppkey, l_partkey);


--
-- Name: n_nk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE UNIQUE INDEX n_nk ON public.nation USING btree (n_nationkey);


--
-- Name: n_rk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX n_rk ON public.nation USING btree (n_regionkey);


--
-- Name: o_ck; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX o_ck ON public.orders USING btree (o_custkey);


--
-- Name: o_od; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX o_od ON public.orders USING btree (o_orderdate);


--
-- Name: o_ok; Type: INDEX; Schema: public; Owner: zheng
--

CREATE UNIQUE INDEX o_ok ON public.orders USING btree (o_orderkey);


--
-- Name: p_pk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE UNIQUE INDEX p_pk ON public.part USING btree (p_partkey);


--
-- Name: ps_pk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX ps_pk ON public.partsupp USING btree (ps_partkey);


--
-- Name: ps_pk_sk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE UNIQUE INDEX ps_pk_sk ON public.partsupp USING btree (ps_partkey, ps_suppkey);


--
-- Name: ps_sk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX ps_sk ON public.partsupp USING btree (ps_suppkey);


--
-- Name: ps_sk_pk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE UNIQUE INDEX ps_sk_pk ON public.partsupp USING btree (ps_suppkey, ps_partkey);


--
-- Name: r_rk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE UNIQUE INDEX r_rk ON public.region USING btree (r_regionkey);


--
-- Name: s_nk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE INDEX s_nk ON public.supplier USING btree (s_nationkey);


--
-- Name: s_sk; Type: INDEX; Schema: public; Owner: zheng
--

CREATE UNIQUE INDEX s_sk ON public.supplier USING btree (s_suppkey);


--
-- Name: customer customer_c_w_id_c_d_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_c_w_id_c_d_id_fkey FOREIGN KEY (c_w_id, c_d_id) REFERENCES public.district(d_w_id, d_id) ON DELETE CASCADE;


--
-- Name: district district_d_w_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.district
    ADD CONSTRAINT district_d_w_id_fkey FOREIGN KEY (d_w_id) REFERENCES public.warehouse(w_id) ON DELETE CASCADE;


--
-- Name: history history_h_c_w_id_h_c_d_id_h_c_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.history
    ADD CONSTRAINT history_h_c_w_id_h_c_d_id_h_c_id_fkey FOREIGN KEY (h_c_w_id, h_c_d_id, h_c_id) REFERENCES public.customer(c_w_id, c_d_id, c_id) ON DELETE CASCADE;


--
-- Name: history history_h_w_id_h_d_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.history
    ADD CONSTRAINT history_h_w_id_h_d_id_fkey FOREIGN KEY (h_w_id, h_d_id) REFERENCES public.district(d_w_id, d_id) ON DELETE CASCADE;


--
-- Name: lineitem lineitem_l_orderkey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.lineitem
    ADD CONSTRAINT lineitem_l_orderkey_fkey FOREIGN KEY (l_orderkey) REFERENCES public.orders(o_orderkey) ON DELETE CASCADE;


--
-- Name: lineitem lineitem_l_partkey_l_suppkey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.lineitem
    ADD CONSTRAINT lineitem_l_partkey_l_suppkey_fkey FOREIGN KEY (l_partkey, l_suppkey) REFERENCES public.partsupp(ps_partkey, ps_suppkey) ON DELETE CASCADE;


--
-- Name: nation nation_n_regionkey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.nation
    ADD CONSTRAINT nation_n_regionkey_fkey FOREIGN KEY (n_regionkey) REFERENCES public.region(r_regionkey) ON DELETE CASCADE;


--
-- Name: new_order new_order_no_w_id_no_d_id_no_o_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.new_order
    ADD CONSTRAINT new_order_no_w_id_no_d_id_no_o_id_fkey FOREIGN KEY (no_w_id, no_d_id, no_o_id) REFERENCES public.oorder(o_w_id, o_d_id, o_id) ON DELETE CASCADE;


--
-- Name: oorder oorder_o_w_id_o_d_id_o_c_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.oorder
    ADD CONSTRAINT oorder_o_w_id_o_d_id_o_c_id_fkey FOREIGN KEY (o_w_id, o_d_id, o_c_id) REFERENCES public.customer(c_w_id, c_d_id, c_id) ON DELETE CASCADE;


--
-- Name: order_line order_line_ol_supply_w_id_ol_i_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.order_line
    ADD CONSTRAINT order_line_ol_supply_w_id_ol_i_id_fkey FOREIGN KEY (ol_supply_w_id, ol_i_id) REFERENCES public.stock(s_w_id, s_i_id) ON DELETE CASCADE;


--
-- Name: order_line order_line_ol_w_id_ol_d_id_ol_o_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.order_line
    ADD CONSTRAINT order_line_ol_w_id_ol_d_id_ol_o_id_fkey FOREIGN KEY (ol_w_id, ol_d_id, ol_o_id) REFERENCES public.oorder(o_w_id, o_d_id, o_id) ON DELETE CASCADE;


--
-- Name: partsupp partsupp_ps_partkey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.partsupp
    ADD CONSTRAINT partsupp_ps_partkey_fkey FOREIGN KEY (ps_partkey) REFERENCES public.part(p_partkey) ON DELETE CASCADE;


--
-- Name: partsupp partsupp_ps_suppkey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.partsupp
    ADD CONSTRAINT partsupp_ps_suppkey_fkey FOREIGN KEY (ps_suppkey) REFERENCES public.supplier(s_suppkey) ON DELETE CASCADE;


--
-- Name: stock stock_s_i_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT stock_s_i_id_fkey FOREIGN KEY (s_i_id) REFERENCES public.item(i_id) ON DELETE CASCADE;


--
-- Name: stock stock_s_w_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.stock
    ADD CONSTRAINT stock_s_w_id_fkey FOREIGN KEY (s_w_id) REFERENCES public.warehouse(w_id) ON DELETE CASCADE;


--
-- Name: supplier supplier_s_nationkey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: zheng
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT supplier_s_nationkey_fkey FOREIGN KEY (s_nationkey) REFERENCES public.nation(n_nationkey) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

