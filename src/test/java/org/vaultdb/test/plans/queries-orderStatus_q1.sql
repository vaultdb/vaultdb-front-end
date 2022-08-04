-- 0
SELECT *, NOT (ol_o_id = 1397 AND (ol_d_id = 3 AND ol_w_id = 1)) AS dummy_tag
FROM (SELECT ol_w_id, ol_d_id, ol_o_id, ol_i_id, ol_delivery_d, ol_amount, ol_supply_w_id, ol_quantity
FROM order_line) AS t
ORDER BY ol_w_id, ol_d_id, ol_o_id, ol_i_id, ol_delivery_d, ol_amount, ol_supply_w_id, ol_quantity
