-- 재귀 깊이 제한 설정
SET @@cte_max_recursion_depth = 100000;

-- BRAND 샘플 데이터 5,000건 생성 (Recursive CTE)
INSERT INTO brands (brand_name, description)
SELECT
    CONCAT('Brand ', n),
    CONCAT('Description for brand ', n)
FROM (
    WITH RECURSIVE seq AS (
        SELECT 1 AS n
        UNION ALL
        SELECT n + 1 FROM seq WHERE n < 5000
    )
    SELECT n FROM seq
) sub;


-- PRODUCT 샘플 데이터 100,000건 생성 (Recursive CTE)
INSERT INTO products (ref_brand_id, product_name, base_price, created_at)
SELECT
    FLOOR(1 + RAND() * 5000) AS brand_id,
    CONCAT('Sample Product ', n) AS product_name,
    FLOOR(1000 + RAND() * 99000) AS base_price,
    NOW()
FROM (
	WITH RECURSIVE seq AS (
	    SELECT 1 AS n
	    UNION ALL
	    SELECT n + 1 FROM seq WHERE n < 100000
    )
    SELECT n FROM seq
) sub;