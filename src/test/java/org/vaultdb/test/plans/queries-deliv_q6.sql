-- 0
SELECT c_w_id, c_d_id, c_id, c_discount, c_credit, c_last, c_first, c_credit_lim, c_balance, c_ytd_payment, c_payment_cnt, c_delivery_cnt, c_street_1, c_street_2, c_city, c_state, c_zip, c_phone, c_since, c_middle, c_data, c_balance + 47440.71875 AS EXPR0, c_delivery_cnt + 1 AS EXPR1, NOT (c_w_id = 1 AND (c_d_id = 4 AND c_id = 2697)) AS dummy_tag
FROM customer
ORDER BY c_w_id, c_d_id, c_id, c_discount, c_credit_lim, c_balance, c_ytd_payment, c_payment_cnt, c_delivery_cnt, c_since
