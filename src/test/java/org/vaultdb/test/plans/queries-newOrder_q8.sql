-- 0
SELECT s_w_id, s_i_id, s_quantity, s_ytd, s_order_cnt, s_remote_cnt, s_data, s_dist_01, s_dist_02, s_dist_03, s_dist_04, s_dist_05, s_dist_06, s_dist_07, s_dist_08, s_dist_09, s_dist_10, 67 AS EXPR0, s_ytd + 3 AS EXPR1, s_order_cnt + 1 AS EXPR2, s_remote_cnt + 0 AS EXPR3, NOT (s_i_id = 39653 AND s_w_id = 1) AS dummy_tag
FROM stock
ORDER BY s_w_id, s_i_id, s_quantity, s_ytd, s_order_cnt, s_remote_cnt
