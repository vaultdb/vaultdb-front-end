-- 0
SELECT ol_w_id, ol_d_id, ol_o_id, ol_number, ol_i_id, ol_delivery_d, ol_amount, ol_supply_w_id, ol_quantity, ol_dist_info, '2022-07-30 04:51:39.807+00' AS EXPR0, NOT (ol_o_id = 2101 AND (ol_d_id = 4 AND ol_w_id = 1)) AS dummy_tag
FROM order_line
ORDER BY ol_w_id, ol_d_id, ol_o_id, ol_number, ol_i_id, ol_delivery_d, ol_amount, ol_supply_w_id, ol_quantity
