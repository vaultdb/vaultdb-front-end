-- 0
SELECT w_id, w_ytd, w_tax, w_name, w_street_1, w_street_2, w_city, w_state, w_zip, w_ytd + 4772.97998046875 AS EXPR0, NOT w_id = 1 AS dummy_tag
FROM warehouse
ORDER BY w_id, w_ytd, w_tax
