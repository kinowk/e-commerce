# 01. 요구사항 명세

> 설계 범위: 상품/브랜드/좋아요/주문 도메인 (회원가입·포인트 충전은 1주차 구현 완료)

---

## 유비쿼터스 언어

| 한국어 | 영문 | 설명 |
|--------|------|------|
| 브랜드 | Brand | 상품을 판매하는 브랜드 |
| 상품 | Product | 브랜드가 판매하는 개별 상품 |
| 재고 | Stock | 상품의 구매 가능 수량 |
| 좋아요 | Like | 유저가 상품에 대해 관심을 표시하는 행위 |
| 주문 | Order | 유저가 하나 이상의 상품을 구매하는 행위 |
| 주문 항목 | OrderItem | 주문 내 개별 상품 및 수량 |
| 외부 전송 | ExternalOrderSync | 주문 완료 후 외부 시스템으로 주문 정보를 전달하는 행위 |

---

## 도메인별 유저 시나리오 및 기능 요구사항

---

### 1. 브랜드 (Brand)

#### 유저 스토리
- 사용자는 특정 브랜드의 정보를 조회할 수 있다.

#### API
| Method | URI | 설명 |
|--------|-----|------|
| GET | `/api/v1/brands/{brandId}` | 브랜드 정보 조회 |

#### 기능 흐름

**브랜드 조회**

```
Main Flow:
1. 사용자가 brandId를 path variable로 요청
2. 해당 브랜드가 존재하면 브랜드 정보(이름, 설명) 반환

Exception Flow:
- 존재하지 않는 brandId → 404 Not Found
```

#### 제약사항
- 브랜드 데이터는 사전 등록되어 있다고 가정 (관리자 등록 API 불필요)

---

### 2. 상품 (Product)

#### 유저 스토리
- 사용자는 상품 목록을 여러 조건으로 조회할 수 있다.
- 사용자는 상품 상세 정보를 조회할 수 있다.
- 상품 목록/상세 조회 시 총 좋아요 수를 확인할 수 있다.

#### API
| Method | URI | 설명 |
|--------|-----|------|
| GET | `/api/v1/products` | 상품 목록 조회 |
| GET | `/api/v1/products/{productId}` | 상품 상세 조회 |

#### 쿼리 파라미터 (목록 조회)
| 파라미터 | 예시 | 설명 | 필수 |
|---------|------|------|------|
| `brandId` | `1` | 특정 브랜드의 상품 필터링 | 선택 |
| `sort` | `latest` / `price_asc` / `likes_desc` | 정렬 기준 (기본값: `latest`) | 선택 |
| `page` | `0` | 페이지 번호 (기본값: 0) | 선택 |
| `size` | `20` | 페이지당 상품 수 (기본값: 20) | 선택 |

#### 기능 흐름

**상품 목록 조회**

```
Main Flow:
1. 사용자가 필터/정렬/페이지 파라미터와 함께 목록 요청
2. 조건에 맞는 상품 목록과 좋아요 수를 포함해 반환

Exception Flow:
- 유효하지 않은 sort 값 → 400 Bad Request
- 유효하지 않은 brandId → 빈 목록 반환 (404 아님)
```

**상품 상세 조회**

```
Main Flow:
1. 사용자가 productId로 요청
2. 상품 정보(이름, 가격, 설명, 재고, 좋아요 수, 브랜드명) 반환

Exception Flow:
- 존재하지 않는 productId → 404 Not Found
```

#### 제약사항
- 상품 데이터는 사전 등록 (관리자 등록 API 불필요)
- 재고(stock)가 0인 상품도 조회는 가능 (품절 상태 표시)
- 좋아요 수는 실시간 집계값이 아닌 denormalized 컬럼으로 관리

---

### 3. 좋아요 (Like)

#### 유저 스토리
- 사용자는 마음에 드는 상품에 좋아요를 누를 수 있다.
- 이미 좋아요한 상품을 다시 좋아요하면 에러 없이 현재 상태를 유지한다 (멱등).
- 좋아요를 취소할 수 있다.
- 좋아요하지 않은 상품을 취소해도 에러 없이 처리된다 (멱등).
- 자신이 좋아요한 상품 목록을 조회할 수 있다.

#### API
| Method | URI | 설명 |
|--------|-----|------|
| POST | `/api/v1/like/products/{productId}` | 상품 좋아요 등록 |
| DELETE | `/api/v1/like/products/{productId}` | 상품 좋아요 취소 |
| GET | `/api/v1/like/products` | 내가 좋아요한 상품 목록 조회 |

#### 기능 흐름

**좋아요 등록 (멱등)**

```
Main Flow:
1. X-USER-ID 헤더로 유저 식별
2. 해당 상품에 대한 좋아요 존재 여부 확인
3. (없으면) Like 레코드 저장, Product.likeCount 증가
4. (있으면) 이미 좋아요 상태 → 200 응답으로 처리 (에러 없음)

Exception Flow:
- 존재하지 않는 productId → 404 Not Found
- X-USER-ID 헤더 없음 → 400 Bad Request
```

**좋아요 취소 (멱등)**

```
Main Flow:
1. X-USER-ID 헤더로 유저 식별
2. 해당 상품에 대한 좋아요 존재 여부 확인
3. (있으면) Like 레코드 삭제, Product.likeCount 감소
4. (없으면) 이미 취소 상태 → 200 응답으로 처리 (에러 없음)

Exception Flow:
- 존재하지 않는 productId → 404 Not Found
- X-USER-ID 헤더 없음 → 400 Bad Request
```

**좋아요한 상품 목록 조회**

```
Main Flow:
1. X-USER-ID 헤더로 유저 식별
2. 해당 유저의 모든 Like 레코드에 연결된 상품 반환

Exception Flow:
- X-USER-ID 헤더 없음 → 400 Bad Request
- 좋아요 상품이 없으면 빈 목록 반환
```

#### 제약사항
- 한 유저는 동일 상품에 하나의 좋아요만 가질 수 있다 (DB unique 제약)
- 좋아요 수는 Like 등록/취소 시점에 Product.likeCount에 반영

---

### 4. 주문 (Order)

#### 유저 스토리
- 사용자는 여러 상품을 한 번에 주문할 수 있다.
- 주문 시 재고가 차감되고, 포인트가 차감되며, 외부 시스템으로 전송된다.
- 자신의 주문 목록 및 주문 상세를 조회할 수 있다.

#### API
| Method | URI | 설명 |
|--------|-----|------|
| POST | `/api/v1/orders` | 주문 요청 |
| GET | `/api/v1/orders` | 주문 목록 조회 |
| GET | `/api/v1/orders/{orderId}` | 단일 주문 상세 조회 |

#### 요청 형식
```json
{
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ]
}
```

#### 기능 흐름

**주문 생성**

```
Main Flow:
1. X-USER-ID 헤더로 유저 식별
2. 요청된 모든 상품에 대해 재고 확인
3. 전체 주문 금액 계산 (상품 가격 × 수량의 합계)
4. 유저 보유 포인트 확인 (총 금액 이상이어야 함)
5. 모든 상품 재고 차감
6. 유저 포인트 차감
7. Order / OrderItem 저장 (상태: PAID)
8. 외부 시스템으로 주문 정보 전송 (Mock)
9. 주문 정보 반환

Exception Flow:
- items 비어있음 → 400 Bad Request
- 존재하지 않는 productId 포함 → 404 Not Found
- 특정 상품 재고 부족 → 409 Conflict
- 포인트 잔액 부족 → 409 Conflict
- X-USER-ID 헤더 없음 → 400 Bad Request
```

**주문 목록 조회**

```
Main Flow:
1. X-USER-ID 헤더로 유저 식별
2. 해당 유저의 모든 주문 목록 반환 (최신순)

Exception Flow:
- X-USER-ID 헤더 없음 → 400 Bad Request
- 주문이 없으면 빈 목록 반환
```

**주문 상세 조회**

```
Main Flow:
1. X-USER-ID 헤더로 유저 식별
2. orderId로 주문 조회
3. 주문 항목 포함 상세 정보 반환

Exception Flow:
- 존재하지 않는 orderId → 404 Not Found
- 다른 유저의 주문 조회 시도 → 403 Forbidden
- X-USER-ID 헤더 없음 → 400 Bad Request
```

#### 제약사항
- 주문 취소 기능은 이번 범위에서 제외
- 재고 차감은 비관적 락(Pessimistic Lock) 적용 고려 (동시 주문 시 정합성)
- 외부 시스템 전송 실패 시 주문은 이미 완료 상태 → Mock이므로 항상 성공으로 처리
- 포인트 차감은 기존 낙관적 락(Optimistic Lock) 구조 재사용
