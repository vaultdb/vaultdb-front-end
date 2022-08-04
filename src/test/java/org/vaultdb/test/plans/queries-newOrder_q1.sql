-- 0
SELECT *, NOT w_id = 1 AS dummy_tag
FROM (SELECT w_id, w_tax
FROM warehouse) AS t
ORDER BY w_id, w_tax
