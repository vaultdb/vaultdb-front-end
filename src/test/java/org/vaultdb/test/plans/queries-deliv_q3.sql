-- 0
SELECT o_w_id, o_d_id, o_id, o_c_id, o_carrier_id, o_ol_cnt, o_all_local, o_entry_d, 1 AS EXPR0, NOT (o_id = 2101 AND (o_d_id = 4 AND o_w_id = 1)) AS dummy_tag
FROM oorder
ORDER BY o_w_id, o_d_id, o_id, o_c_id, o_carrier_id, o_ol_cnt, o_all_local, o_entry_d
