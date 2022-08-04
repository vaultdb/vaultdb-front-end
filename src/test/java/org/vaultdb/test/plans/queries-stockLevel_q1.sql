-- 0
SELECT ol_i_id, NOT (ol_w_id = 1 AND ol_d_id = 1 AND (ol_o_id < 3004 AND ol_o_id >= 2984)) AS dummy_tag
FROM (SELECT ol_w_id, ol_d_id, ol_o_id, ol_i_id
FROM order_line) AS t
ORDER BY ol_w_id, ol_d_id, ol_o_id, ol_i_id
-- 1
SELECT s_i_id, NOT (s_w_id = 1 AND s_quantity < 10) AS dummy_tag
FROM (SELECT s_w_id, s_i_id, s_quantity
FROM stock) AS t
ORDER BY s_w_id, s_i_id, s_quantity
