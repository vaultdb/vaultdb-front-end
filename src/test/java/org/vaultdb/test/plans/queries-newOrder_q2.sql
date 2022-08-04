-- 0
SELECT *, NOT (d_w_id = 1 AND d_id = 4) AS dummy_tag
FROM (SELECT d_w_id, d_id, d_tax, d_next_o_id
FROM district) AS t
ORDER BY d_w_id, d_id, d_tax, d_next_o_id
