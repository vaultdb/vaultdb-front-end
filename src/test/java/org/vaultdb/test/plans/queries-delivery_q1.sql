-- 0
SELECT *, NOT (no_o_id = 2101 AND (no_d_id = 4 AND no_w_id = 1)) AS dummy_tag
FROM new_order
