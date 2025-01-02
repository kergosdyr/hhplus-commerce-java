# e-커머스 서비스 API 문서

## 목차

1. [잔액 충전/조회](#1-잔액-충전조회)
    - [1.1 잔액 충전](#11-잔액-충전)
    - [1.2 잔액 조회](#12-잔액-조회)
2. [상품 조회](#2-상품-조회)
3. [선착순 쿠폰 기능](#3-선착순-쿠폰-기능)
    - [3.1 쿠폰 발급](#31-쿠폰-발급)
    - [3.2 보유 쿠폰 조회](#32-보유-쿠폰-조회)
4. [주문/결제](#4-주문결제)
5. [인기 상품 조회](#5-인기-상품-조회)

---

## 1. 잔액 충전/조회

### 1.1 잔액 충전

#### Description

- 사용자 잔액을 일정 금액만큼 **충전**합니다.

#### Request

- **URL**: `POST /api/v1/balance/charge`
- **Method**: `POST`
- **Headers**:
    - `Authorization: Bearer <JWT-ACCESS-TOKEN>`
    - `Content-Type: application/json`
- **Body**:
  ```json
  {
    "userId": 123,
    "amount": 50000
  }
  ```

| 필드명    | 타입     | 설명      | 필수 여부 |
|--------|--------|---------|-------|
| userId | number | 사용자 식별자 | O     |
| amount | number | 충전할 금액  | O     |

#### Response

##### 성공 (HTTP 200)

```json
{
  "result": "SUCCESS",
  "data": {
    "userId": 123,
    "balance": 150000
  },
  "error": null
}
```

| 필드명     | 타입     | 설명      |
|---------|--------|---------|
| userId  | number | 사용자 식별자 |
| balance | number | 충전 후 잔액 |

##### 에러

###### 1) 잘못된 요청 (HTTP 400)

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E400",
    "message": "충전 금액이 잘못되었습니다.",
    "data": null
  }
}
```

###### 2) 서버 오류 (HTTP 500)

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E500",
    "message": "잔액 충전 처리 중 오류가 발생했습니다.",
    "data": null
  }
}
```

---

### 1.2 잔액 조회

#### Description

- 사용자 **현재 잔액**을 조회.

#### Request

- **URL**: `GET /api/v1/balance/{userId}`
- **Method**: `GET`
- **Headers**:
    - `Authorization: Bearer <JWT-ACCESS-TOKEN>`
- **Path Variable**:
    - `userId`: number (사용자 ID)

#### Response

##### 성공 (HTTP 200)

```json
{
  "result": "SUCCESS",
  "data": {
    "userId": 123,
    "balance": 150000
  },
  "error": null
}
```

| 필드명     | 타입     | 설명      |
|---------|--------|---------|
| userId  | number | 사용자 식별자 |
| balance | number | 현재 잔액   |

##### 에러

###### 1) 잘못된 요청 (HTTP 400)

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E400",
    "message": "존재하지 않는 사용자입니다.",
    "data": null
  }
}
```

###### 2) 서버 오류 (HTTP 500)

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E500",
    "message": "잔액 조회 처리 중 오류가 발생했습니다.",
    "data": null
  }
}
```

---

## 2. 상품 조회

### Description

- 판매 중인 상품 목록 조회.
- 각 상품의 ID, 이름, 가격, 재고 정보를 반환.

### Request

- **URL**: `GET /api/v1/products`
- **Method**: `GET`
- **Headers**:
    - `Authorization: Bearer <JWT-ACCESS-TOKEN>`
- **Query Params** (옵션):
    - `page`: number (페이지 번호)
    - `size`: number (페이지 크기)
    - `keyword`: string (상품명 검색어)
- **Example**: `/api/v1/products?page=1&size=10&keyword=ipad`

#### Response

##### 성공 (HTTP 200)

```json
{
  "result": "SUCCESS",
  "data": {
    "items": [
      {
        "id": 1,
        "name": "Apple iPad",
        "price": 500000,
        "stock": 100
      },
      {
        "id": 2,
        "name": "Samsung Galaxy Tab",
        "price": 400000,
        "stock": 50
      }
    ],
    "pageInfo": {
      "currentPage": 1,
      "totalPages": 5,
      "totalItems": 10
    }
  },
  "error": null
}
```

##### 에러

###### 1) 잘못된 요청 (HTTP 400)

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E400",
    "message": "검색 파라미터가 잘못되었습니다.",
    "data": null
  }
}
```

###### 2) 서버 오류 (HTTP 500)

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E500",
    "message": "상품 목록 조회 중 오류가 발생했습니다.",
    "data": null
  }
}
```

---

## 3. 선착순 쿠폰 기능

### 3.1 쿠폰 발급

#### Description

- **선착순** 쿠폰을 발급.
- 쿠폰 재고가 소진되면 발급 불가.

#### Request

- **URL**: `POST /api/v1/coupons/issue`
- **Method**: `POST`
- **Headers**:
    - `Authorization: Bearer <JWT-ACCESS-TOKEN>`
    - `Content-Type: application/json`
- **Body**:
  ```json
  {
    "userId": 123,
    "couponId": 999
  }
  ```

| 필드명      | 타입     | 설명     | 필수 여부 |
|----------|--------|--------|-------|
| userId   | number | 사용자 ID | O     |
| couponId | number | 쿠폰 ID  | O     |

#### Response

##### 성공 (HTTP 200)

```json
{
  "result": "SUCCESS",
  "data": {
    "userCouponId": 111,
    "userId": 123,
    "couponId": 999,
    "issuedAt": "2024-01-01T10:00:00",
    "status": "ISSUED"
  },
  "error": null
}
```

##### 에러

###### 1) 잘못된 요청 (HTTP 400) - 이미 쿠폰 발급됨

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E400",
    "message": "이미 쿠폰을 발급받았습니다.",
    "data": null
  }
}
```

###### 2) 잘못된 요청 (HTTP 400) - 쿠폰 재고 소진

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E400",
    "message": "쿠폰 재고가 모두 소진되었습니다.",
    "data": null
  }
}
```

###### 3) 서버 오류 (HTTP 500)

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E500",
    "message": "쿠폰 발급 처리 중 오류가 발생했습니다.",
    "data": null
  }
}
```

---

### 3.2 보유 쿠폰 조회

#### Description

- 유저가 현재 보유 중인 쿠폰 목록을 조회.
- 사용 여부, 만료 정보 등 함께 반환.

#### Request

- **URL**: `GET /api/v1/coupons/users/{userId}`
- **Method**: `GET`
- **Headers**:
    - `Authorization: Bearer <JWT-ACCESS-TOKEN>`
- **Path Variable**:
    - `userId`: number (사용자 ID)
- **Example**: `/api/v1/coupons/users/123`

#### Response

##### 성공 (HTTP 200)

```json
{
  "result": "SUCCESS",
  "data": {
    "userId": 123,
    "coupons": [
      {
        "userCouponId": 111,
        "couponId": 999,
        "couponName": "10% Discount",
        "amount": 10,
        "isUsed": false,
        "expiredAt": "2024-01-31T23:59:59"
      },
      {
        "userCouponId": 112,
        "couponId": 1000,
        "couponName": "3000원 할인 쿠폰",
        "amount": 3000,
        "isUsed": true,
        "expiredAt": "2024-02-15T23:59:59"
      }
    ]
  },
  "error": null
}
```

##### 에러

###### 1) 잘못된 요청 (HTTP 400) - 유저가 존재하지 않음

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E400",
    "message": "존재하지 않는 사용자입니다.",
    "data": null
  }
}
```

###### 2) 서버 오류 (HTTP 500)

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E500",
    "message": "쿠폰 조회 중 오류가 발생했습니다.",
    "data": null
  }
}
```

---

## 4. 주문/결제

### Description

- 여러 상품을 동시에 주문하고, 유저 잔액을 차감하여 결제.
- 쿠폰 사용 시 할인 적용.
- 성공 시 **외부 데이터 플랫폼**으로 주문 정보를 전송(Mock).

### Request

- **URL**: `POST /api/v1/order`
- **Method**: `POST`
- **Headers**:
    - `Authorization: Bearer <JWT-ACCESS-TOKEN>`
    - `Content-Type: application/json`
- **Body**:
  ```json
  {
    "userId": 123,
    "orderItems": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 3
      }
    ],
    "couponId": 999
  }
  ```

| 필드명        | 타입     | 설명         | 필수 여부 |
|------------|--------|------------|-------|
| userId     | number | 사용자 식별자    | O     |
| orderItems | array  | 상품 주문 목록   | O     |
| couponId   | number | 쿠폰 ID (옵션) | X     |

#### Response

##### 성공 (HTTP 200)

```json
{
  "result": "SUCCESS",
  "data": {
    "orderId": 20240001,
    "userId": 123,
    "orderStatus": "PAID",
    "totalAmount": 150000,
    "discountAmount": 15000,
    "paidAmount": 135000,
    "paymentId": 50001,
    "paymentStatus": "SUCCESS",
    "orderItems": [
      {
        "productId": 1,
        "quantity": 2,
        "price": 50000
      },
      {
        "productId": 2,
        "quantity": 3,
        "price": 20000
      }
    ],
    "createdAt": "2024-01-01T10:00:00"
  },
  "error": null
}
```

##### 에러

###### 1) 잘못된 요청 (HTTP 400) - 상품/수량 오류

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E400",
    "message": "주문 정보가 유효하지 않습니다.",
    "data": null
  }
}
```

###### 2) 잘못된 요청 (HTTP 400) - 재고 부족

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E400",
    "message": "재고가 부족하여 주문할 수 없습니다.",
    "data": null
  }
}
```

###### 3) 잘못된 요청 (HTTP 400) - 잔액 부족

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E400",
    "message": "잔액이 부족합니다.",
    "data": null
  }
}
```

###### 4) 서버 오류 (HTTP 500)

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E500",
    "message": "주문 처리 중 알 수 없는 오류가 발생했습니다.",
    "data": null
  }
}
```

---

## 5. 상위 상품 조회

### Description

- **최근 3일** 가장 많이 팔린 상위 5개 상품 조회.
- 내부 통계 테이블/캐시/실시간 집계 등은 구현에 따라 자유.

### Request

- **URL**: `GET /api/v1/products/top-sellers`
- **Method**: `GET`
- **Headers**:
    - `Authorization: Bearer <JWT-ACCESS-TOKEN>`
- **Query Params**:
    - `days`: number (며칠간 판매량 기준, 기본값: 3)
- **Example**: `/api/v1/products/top-sellers?days=3`

#### Response

##### 성공 (HTTP 200)

```json
{
  "result": "SUCCESS",
  "data": {
    "periodDays": 3,
    "topSellers": [
      {
        "id": 1,
        "name": "Apple iPad",
        "totalSold": 150
      },
      {
        "id": 5,
        "name": "MacBook Air",
        "totalSold": 100
      },
      {
        "id": 2,
        "name": "Galaxy Tab",
        "totalSold": 80
      }
    ]
  },
  "error": null
}
```

##### 에러

###### 1) 잘못된 요청 (HTTP 400)

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E400",
    "message": "조회 기준이 유효하지 않습니다.",
    "data": null
  }
}
```

###### 2) 서버 오류 (HTTP 500)

```json
{
  "result": "ERROR",
  "data": null,
  "error": {
    "code": "E500",
    "message": "인기 상품 조회 중 오류가 발생했습니다.",
    "data": null
  }
}
```
