-- 0
SELECT o_id, o_carrier_id, o_entry_d, NOT (o_w_id = 1 AND (o_d_id = 3 AND o_c_id = 463)) AS dummy_tag
FROM (SELECT o_w_id, o_d_id, o_id, o_c_id, o_carrier_id, o_entry_d
FROM oorder) AS t
ORDER BY o_w_id, o_d_id, o_id, o_c_id, o_carrier_id, o_entry_d
