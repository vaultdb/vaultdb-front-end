-- 0
SELECT *, NOT (s_i_id = 39653 AND s_w_id = 1) AS dummy_tag
FROM (SELECT s_w_id, s_i_id, s_quantity, s_data, s_dist_01, s_dist_02, s_dist_03, s_dist_04, s_dist_05, s_dist_06, s_dist_07, s_dist_08, s_dist_09, s_dist_10
FROM stock) AS t
ORDER BY s_w_id, s_i_id, s_quantity
