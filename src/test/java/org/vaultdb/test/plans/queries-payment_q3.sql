-- 0
SELECT *, NOT (d_w_id = 1 AND d_id = 5) AS dummy_tag
FROM (SELECT d_w_id, d_id, d_name, d_street_1, d_street_2, d_city, d_state, d_zip
FROM district) AS t
ORDER BY d_w_id, d_id
