-- 0
SELECT d_w_id, d_id, d_ytd, d_tax, d_next_o_id, d_name, d_street_1, d_street_2, d_city, d_state, d_zip, d_next_o_id + 1 AS EXPR0, NOT (d_w_id = 1 AND d_id = 4) AS dummy_tag
FROM district
ORDER BY d_w_id, d_id, d_ytd, d_tax, d_next_o_id
