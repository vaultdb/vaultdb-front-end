-- 0
SELECT ol_amount, NOT (ol_o_id = 2101 AND (ol_d_id = 4 AND ol_w_id = 1)) AS dummy_tag
FROM (SELECT ol_w_id, ol_d_id, ol_o_id, ol_amount
FROM order_line) AS t
ORDER BY ol_w_id, ol_d_id, ol_o_id, ol_amount
