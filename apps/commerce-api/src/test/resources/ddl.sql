-- 1.1. 브랜드(brands) 테이블
CREATE TABLE brands (
    brand_id     BIGINT NOT NULL AUTO_INCREMENT COMMENT '브랜드ID',
    brand_name   VARCHAR(255) NOT NULL COMMENT '브랜드명',
    description  VARCHAR(500) NULL COMMENT '설명',
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at   TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at   TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시',

    PRIMARY KEY (brand_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 상품(products) 테이블
CREATE TABLE products (
    product_id    BIGINT NOT NULL AUTO_INCREMENT COMMENT '상품ID',
    ref_brand_id  BIGINT NOT NULL COMMENT '브랜드ID',
    product_name  VARCHAR(255) NOT NULL COMMENT '상품명',
    base_price    INT NOT NULL COMMENT '기본금액',
    like_count    INT NOT NULL DEFAULT 0
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at    TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    deleted_at    TIMESTAMP NULL DEFAULT NULL COMMENT '삭제일시',

    PRIMARY KEY (product_id)

    --INDEX idx_product_brand_id (brand_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 상품(products) 테이블 index
CREATE INDEX idx_products_brand_price ON products (ref_brand_id, base_price);
CREATE INDEX idx_products_brand_like ON products (ref_brand_id, like_count);
CREATE INDEX idx_products_brand_created ON products (ref_brand_id, created_at);