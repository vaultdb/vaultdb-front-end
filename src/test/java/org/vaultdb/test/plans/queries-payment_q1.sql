-- 0
SELECT *, NOT w_id = 1 AS dummy_tag
FROM (SELECT w_id, w_name, w_street_1, w_street_2, w_city, w_state, w_zip
FROM warehouse) AS t
ORDER BY w_id
