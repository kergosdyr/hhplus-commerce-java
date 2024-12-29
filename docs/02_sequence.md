# e-커머스 서비스 - 시퀀스 다이어그램

## 1. 잔액 충전 / 조회

---
```mermaid
sequenceDiagram
    participant User
    participant API
    participant BalanceService
    participant Database

    User->>API: 잔액 충전 요청 (사용자 ID, 충전 금액)
    API->>BalanceService: 잔액 충전 요청
    BalanceService->>Database: 사용자 잔액 조회 (SELECT)
    Database-->>BalanceService: 현재 잔액
    note right of BalanceService: 트랜잭션 또는 Row-Level Lock<br>동시성 이슈 고려
    BalanceService->>Database: 잔액 업데이트 (UPDATE)
    Database-->>BalanceService: 업데이트 결과
    BalanceService-->>API: 잔액 충전 성공, 총 잔액 반환
    API-->>User: 충전 성공 (총 잔액)

    User->>API: 잔액 조회 요청 (사용자 ID)
    API->>BalanceService: 잔액 조회
    BalanceService->>Database: 사용자 잔액 SELECT
    Database-->>BalanceService: 잔액 정보
    BalanceService-->>API: 잔액 정보
    API-->>User: 현재 잔액
```
- **BalanceService**에서 잔액을 관리하며, DB 트랜잭션/Lock을 통해 동시성 이슈를 방지합니다.
- 충전 후 최종 잔액을 반환합니다.
- 조회는 단순 Select로 사용자의 잔액을 전달합니다.

<br>

## 2. 상품 조회

---
```mermaid
sequenceDiagram
    participant User
    participant API
    participant ProductService
    participant Database

    User->>API: 상품 목록 조회 요청
    API->>ProductService: 상품 목록 조회
    ProductService->>Database: 상품 정보 SELECT (상품명, 가격, 재고 등)
    Database-->>ProductService: 상품 정보 목록
    ProductService-->>API: 상품 목록
    API-->>User: 상품 목록 반환
```
- **ProductService**가 상품 목록을 DB에서 조회하여 반환합니다.
- 사용자에게 상품명, 가격, 재고 등의 정보를 보여줍니다.
- 재고는 주문 시점에 다시 한 번 확인이 필요합니다.

<br>

## 3. 선착순 쿠폰 발급 / 보유 쿠폰 조회

---

### 3-1) 선착순 쿠폰 발급

```mermaid
sequenceDiagram
    participant User
    participant API
    participant CouponService
    participant Database

    User->>API: 선착순 쿠폰 발급 요청
    API->>CouponService: 쿠폰 발급 요청 (사용자 ID)
    CouponService->>Database: 쿠폰 남은 수량 조회 (SELECT)
    Database-->>CouponService: 쿠폰 수량
    alt 재고 있음
        note right of CouponService: 쿠폰 재고 동시 차감<br>트랜잭션 or Lock
        CouponService->>Database: 쿠폰 재고 1 감소 (UPDATE)
        Database-->>CouponService: 성공
        CouponService->>Database: 쿠폰 발급 이력 저장 (INSERT)
        Database-->>CouponService: 발급 저장 완료
        CouponService-->>API: 쿠폰 발급 성공
        API-->>User: 쿠폰 발급 성공
    else 재고 없음
        CouponService-->>API: 쿠폰 발급 실패
        API-->>User: 실패 메시지 (재고 없음)
    end
```
- **CouponService**가 쿠폰 재고를 확인하고, 있을 경우 1 감소 후 발급 이력을 저장합니다.
- 동시에 여러 사용자가 요청할 수 있으므로 DB 트랜잭션/Lock을 통해 선착순 로직을 안전하게 처리해야 합니다.

<br>

### 3-2) 보유 쿠폰 조회

#### 이벤트 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant API
    participant CouponService
    participant Database

    User->>API: 보유 쿠폰 목록 조회
    API->>CouponService: 사용자 쿠폰 목록 조회 (사용자 ID)
    CouponService->>Database: 쿠폰 발급 이력 SELECT
    Database-->>CouponService: 쿠폰 목록
    CouponService-->>API: 쿠폰 목록
    API-->>User: 쿠폰 목록 반환
```
- 사용자 ID로 쿠폰 테이블(또는 발급 이력 테이블)에서 보유 중인 쿠폰 목록을 조회합니다.
- 유효 기간이나 사용 여부 등은 추가 로직으로 확장 가능.

<br>

## 4. 주문 / 결제

---
```mermaid

sequenceDiagram
    participant User
    participant API
    participant OrderService
    participant ProductService
    participant CouponService
    participant PaymentService
    participant BalanceService
    participant Database
    participant AnalyticsService
    participant DataPlatform as DataPlatform(외부)

    User->>API: 주문/결제 요청 (사용자 ID, 상품 목록, 쿠폰 ID 등)
    API->>OrderService: 주문 생성 요청
    note over OrderService: (트랜잭션 시작)

    OrderService->>ProductService: 상품 재고 확인
    ProductService->>Database: 재고 SELECT
    Database-->>ProductService: 재고 정보
    ProductService-->>OrderService: 재고 확인 결과
    alt 쿠폰 사용
        OrderService->>CouponService: 쿠폰 유효성 조회
        CouponService->>Database: 쿠폰 상태 SELECT
        Database-->>CouponService: 쿠폰 정보
        CouponService-->>OrderService: 쿠폰 사용 가능 여부
    else 쿠폰 실패
        note right of OrderService: (트랜잭션 롤백)
        OrderService-->>API: 주문 실패 (쿠폰 사용 불가)
        API-->>User: 쿠폰 사용 불가 메시지
    end

    alt 재고 충분

        OrderService->>Database: 주문 정보 INSERT (상품 목록, 할인금액 등)
        Database-->>OrderService: 주문 ID

        OrderService->>PaymentService: 결제 요청 (결제금액, 쿠폰 정보 등)
        PaymentService->>BalanceService: 사용자 잔액 조회
        BalanceService->>Database: 사용자 잔액 SELECT
        Database-->>BalanceService: 잔액
        BalanceService-->>PaymentService: 잔액 반환

        alt 잔액 충분
            PaymentService->>Database: 결제 이력 INSERT
            Database-->>PaymentService: 결제 이력 생성 완료
            PaymentService->>BalanceService: 잔액 차감
            BalanceService->>Database: 잔액 UPDATE
            Database-->>BalanceService: 차감 완료
            BalanceService-->>PaymentService: 잔액 차감 성공
            PaymentService-->>OrderService: 결제 성공

            OrderService->>Database: 재고 차감 (상품마다 UPDATE)
            Database-->>OrderService: 재고 차감 완료

            alt 쿠폰 사용
                OrderService->>CouponService: 쿠폰 사용 처리 (UPDATE)
                CouponService->>Database: 쿠폰 상태 변경
                Database-->>CouponService: 변경 완료
            else 쿠폰 실패
                note right of OrderService: (트랜잭션 롤백)
                OrderService-->>API: 주문 실패 (쿠폰 사용 불가)
                API-->>User: 쿠폰 사용 불가 메시지
            end


            note right of OrderService: (트랜잭션 커밋)
            OrderService->>AnalyticsService: 결제 성공 주문 정보 전송
            alt 데이터 플랫폼 전송 성공
                AnalyticsService->>DataPlatform: 주문 정보 전송 (REST API)
                DataPlatform-->>AnalyticsService: 전송 성공
                AnalyticsService-->>OrderService: 성공 처리 완료
            else 데이터 플랫폼 전송 실패
                AnalyticsService->>DataPlatform: 주문 정보 전송 (REST API)
                DataPlatform-->>AnalyticsService: 전송 실패
                AnalyticsService-->>OrderService: 실패 처리 (재시도 로직 또는 기록)
            end

            OrderService-->>API: 주문 & 결제 성공 응답
            API-->>User: 주문/결제 성공 (주문 ID 등)
        else 잔액 부족
            note right of OrderService: (트랜잭션 롤백)
            PaymentService-->>OrderService: 결제 실패 (잔액 부족)
            OrderService-->>API: 실패 응답 (잔액 부족)
            API-->>User: 결제 실패 (잔액 부족)
        end
    else 재고 부족
        note right of OrderService: (트랜잭션 롤백)
        OrderService-->>API: 주문 실패 (재고 부족)
        API-->>User: 재고 부족 실패 메시지
    end
```

- **OrderService**가 트랜잭션을 관리하며 쿠폰 유효성 → 재고 확인 → 주문 생성 → 결제 로직을 순차적으로 진행합니다.
- 결제에는 **PaymentService**와 **BalanceService**가 관여하며, 잔액 부족 시 트랜잭션을 롤백합니다.
- 최종적으로 재고 차감, 쿠폰 사용 처리까지 한 번에 처리한 뒤, **AnalyticsService**로 데이터 플랫폼으로 주문 정보를 전송할 수 있습니다.

<br>


## 5. 인기 상품 조회

---

```mermaid
sequenceDiagram
    participant User
    participant API
    participant StatisticsService
    participant Database

    User->>API: 인기 상품 조회 (최근 3일)
    API->>StatisticsService: 인기 상품 조회 요청
    note over StatisticsService: 최근 3일 주문 데이터 SELECT <br>상품별 판매량 집계
    StatisticsService->>Database: 최근 3일 주문 내역 SELECT
    Database-->>StatisticsService: 주문 내역
    note right of StatisticsService: 상품별 판매량 상위 5개 추출
    StatisticsService-->>API: 인기 상품 목록
    API-->>User: 인기 상품 정보
```
- **StatisticsService**(또는 다른 이름)가 최근 3일간 주문 테이블을 조회하여 상품별 판매량을 계산합니다.
- 상위 5개 상품을 선정해 사용자에게 반환합니다.
- 대량 트래픽이 예상될 시 Materialized View 나 비정규화된 특수 테이블을 고려해야합니다. 우선, 단순 SELECT 후 계산으로 가정합니다.
