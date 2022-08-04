-- 0
SELECT *, NOT (c_w_id = 1 AND (c_d_id = 4 AND c_id = 1027)) AS dummy_tag
FROM (SELECT c_w_id, c_d_id, c_id, c_discount, c_credit, c_last
FROM customer) AS t
ORDER BY c_w_id, c_d_id, c_id, c_discount
