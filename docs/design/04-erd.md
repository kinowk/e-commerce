# 04. ERD (Entity Relationship Diagram)

---

## 전체 ERD

```mermaid
erDiagram
    users {
        bigint id PK
        varchar username
        varchar login_id UK
        varchar password
        varchar email
        varchar birth_date
        varchar gender
        datetime created_at
        datetime updated_at
    }

    points {
        bigint id PK
        bigint ref_user_id FK "UK"
        bigint balance
        bigint version
        datetime created_at
        datetime updated_at
    }

    point_histories {
        bigint id PK
        bigint ref_point_id FK
        bigint ref_user_id FK
        bigint amount
        varchar type
        varchar description
        datetime created_at
        datetime updated_at
    }

    brands {
        bigint id PK
        varchar name
        varchar description
        datetime created_at
        datetime updated_at
    }

    products {
        bigint id PK
        bigint brand_id FK
        varchar name
        text description
        bigint price
        bigint stock
        bigint like_count
        varchar status
        bigint version
        datetime created_at
        datetime updated_at
    }

    likes {
        bigint id PK
        varchar user_login_id FK
        bigint product_id FK
        datetime created_at
    }

    orders {
        bigint id PK
        varchar user_login_id FK
        bigint total_amount
        varchar status
        datetime created_at
        datetime updated_at
    }

    order_items {
        bigint id PK
        bigint order_id FK
        bigint product_id FK
        bigint quantity
        bigint unit_price
        bigint total_price
        datetime created_at
        datetime updated_at
    }

    users ||--|| points : "has"
    users ||--o{ point_histories : "has"
    points ||--o{ point_histories : "has"
    brands ||--o{ products : "has"
    users ||--o{ likes : "has"
    products ||--o{ likes : "has"
    users ||--o{ orders : "places"
    orders ||--o{ order_items : "contains"
    products ||--o{ order_items : "included in"
```

---

## 테이블 상세 설명

### users (기존)
| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| login_id | VARCHAR(10) | UNIQUE, NOT NULL | 영문+숫자 10자 이내 |
| username | VARCHAR(50) | NOT NULL | 닉네임 |
| password | VARCHAR(255) | NOT NULL | |
| email | VARCHAR(255) | NOT NULL | xx@yy.zz 형식 |
| birth_date | VARCHAR(10) | NOT NULL | yyyy-MM-dd 형식 |
| gender | VARCHAR(10) | NOT NULL | MALE / FEMALE |

### brands (신규)
| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| name | VARCHAR(100) | NOT NULL | 브랜드명 |
| description | TEXT | | 브랜드 설명 |

### products (신규)
| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| brand_id | BIGINT | FK (brands.id), NOT NULL | |
| name | VARCHAR(200) | NOT NULL | 상품명 |
| description | TEXT | | 상품 설명 |
| price | BIGINT | NOT NULL | 판매 가격 (원) |
| stock | BIGINT | NOT NULL, DEFAULT 0 | 재고 수량 |
| like_count | BIGINT | NOT NULL, DEFAULT 0 | 좋아요 수 (denormalized) |
| status | VARCHAR(20) | NOT NULL | ACTIVE / SOLD_OUT / INACTIVE |
| version | BIGINT | NOT NULL, DEFAULT 0 | 낙관적 락 버전 |

> **설계 고려사항**: `like_count`를 Like 테이블에서 COUNT(*) 쿼리로 실시간 집계하지 않고
> Product 테이블에 비정규화 컬럼으로 유지한다. 조회 성능과 쓰기 정합성의 트레이드오프 중
> 조회 빈도가 압도적으로 높은 이커머스 특성을 감안해 비정규화를 선택했다.

### likes (신규)
| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| user_login_id | VARCHAR(10) | FK (users.login_id), NOT NULL | |
| product_id | BIGINT | FK (products.id), NOT NULL | |
| created_at | DATETIME | NOT NULL | |

- `(user_login_id, product_id)` UNIQUE 제약으로 중복 좋아요 방지
- 좋아요 취소 시 hard delete (soft delete 불필요 - 비즈니스 의미 없음)
- `updated_at` 불필요 (변경 사항이 없는 단순 연결 레코드)

### orders (신규)
| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| user_login_id | VARCHAR(10) | FK (users.login_id), NOT NULL | |
| total_amount | BIGINT | NOT NULL | 주문 총금액 |
| status | VARCHAR(20) | NOT NULL | PENDING / PAID / CANCELLED / FAILED |

> **설계 고려사항**: `user_login_id`를 FK로 직접 참조한다.
> users.id(PK)를 참조하는 것이 일반적이나, 기존 Point·Like 도메인이 모두
> `login_id`로 유저를 식별하는 패턴을 따르므로 일관성 유지.

### order_items (신규)
| 컬럼 | 타입 | 제약 | 설명 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | |
| order_id | BIGINT | FK (orders.id), NOT NULL | |
| product_id | BIGINT | FK (products.id), NOT NULL | |
| quantity | BIGINT | NOT NULL | 주문 수량 |
| unit_price | BIGINT | NOT NULL | 주문 시점 단가 (스냅샷) |
| total_price | BIGINT | NOT NULL | unit_price × quantity |

> **설계 고려사항**: `unit_price`를 product.price에서 가져오지 않고 주문 시점에 스냅샷으로 저장한다.
> 이후 상품 가격 변경이 과거 주문에 영향을 주지 않도록 하기 위함.

---

## 인덱스 설계

| 테이블 | 인덱스 | 이유 |
|--------|--------|------|
| products | `(brand_id)` | 브랜드 필터 조회 |
| products | `(status, like_count)` | 좋아요순 정렬 |
| products | `(status, price)` | 가격순 정렬 |
| products | `(status, created_at)` | 최신순 정렬 (기본값) |
| likes | `(user_login_id)` | 유저별 좋아요 목록 조회 |
| likes | `UNIQUE (user_login_id, product_id)` | 중복 방지 + 존재 여부 조회 |
| orders | `(user_login_id, created_at DESC)` | 유저별 주문 목록 최신순 |
| order_items | `(order_id)` | 주문 상세 조회 시 항목 로드 |
