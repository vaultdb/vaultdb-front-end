-- 0
SELECT *, NOT (o_id = 2101 AND (o_d_id = 4 AND o_w_id = 1)) AS dummy_tag
FROM (SELECT o_w_id, o_d_id, o_id, o_c_id
FROM oorder) AS t
ORDER BY o_w_id, o_d_id, o_id, o_c_id
