-- 0
SELECT c_id, c_discount, c_credit, c_first, c_credit_lim, c_balance, c_ytd_payment, c_payment_cnt, c_street_1, c_street_2, c_city, c_state, c_zip, c_phone, c_since, c_middle, NOT (c_w_id = 1 AND (c_d_id = 3 AND c_last = 'PRESANTIABLE')) AS dummy_tag
FROM (SELECT c_w_id, c_d_id, c_id, c_discount, c_credit, c_last, c_first, c_credit_lim, c_balance, c_ytd_payment, c_payment_cnt, c_street_1, c_street_2, c_city, c_state, c_zip, c_phone, c_since, c_middle
FROM customer) AS t
ORDER BY c_w_id, c_d_id, c_id, c_discount, c_credit_lim, c_balance, c_ytd_payment, c_payment_cnt, c_since