-- 0
SELECT *, NOT i_id = 39653 AS dummy_tag
FROM (SELECT i_id, i_name, i_price, i_data
FROM item) AS t
ORDER BY i_id, i_price
