# e-커머스 서비스 - 시퀀스 다이어그램

## 잔액 충전

---
```mermaid
sequenceDiagram
    participant User
    participant API
    participant Balance
    participant Database
    User ->> API: [POST] 잔액 충전 (userId, chargeAmount)
    note over API: 1) 사용자 유효성 검증
    alt 유효한 사용자
        note over API: 2) 금액 유효성 검증
        alt 금액이 유효
            API ->> Balance: 충전 처리
            note right of Balance: 트랜잭션 시작<br>Lock(동시성 이슈 고려)
            Balance ->> Database: 사용자 잔액 SELECT
            Database -->> Balance: 잔액 정보
            Balance ->> Database: 잔액 UPDATE (+chargeAmount)
            Database -->> Balance: 업데이트 결과
            note right of Balance: 트랜잭션 커밋
            Balance -->> API: 충전 성공 (최종 잔액)
            API -->> User: 정상 응답 (최종 잔액)
        else 금액이 유효하지 않음
            API -->> User: 오류 응답 ("유효하지 않은 금액")
        end
    else 유효하지 않은 사용자
        API -->> User: 오류 응답 ("유효하지 않은 사용자")
    end
```

- **Balance**에서 잔액을 관리하며, DB 트랜잭션/Lock을 통해 동시성 이슈를 방지합니다.
- 충전 후 최종 잔액을 반환합니다.
- 조회는 단순 Select로 사용자의 잔액을 전달합니다.

<br>

## 잔액 조회

---

```mermaid
sequenceDiagram
    participant User
    participant API
    participant Balance
    participant Database
    User ->> API: [GET] 잔액 조회 (userId)
    note over API: 1) 사용자 유효성 검증
    alt 유효한 사용자
        API ->> Balance: 잔액 조회
        Balance ->> Database: SELECT 잔액
        Database -->> Balance: 잔액 정보
        Balance -->> API: 잔액 정보
        API -->> User: 정상 응답 (현재 잔액)
    else 유효하지 않은 사용자
        API -->> User: 오류 응답 ("유효하지 않은 사용자")
    end
```

- **Balance**에서 사용자의 잔액을 조회합니다.
- 사용자 ID로 DB에서 잔액 정보를 조회하여 반환합니다.
- 사용자가 유효하지 않을 경우 오류 응답을 반환합니다.

## 상품 조회

---
```mermaid
sequenceDiagram
    participant User
    participant API
    participant Product
    participant Database
    User ->> API: [GET] 상품 조회 (productId)
    note over API: 1) 상품 ID 유효성 검증
    alt 유효한 상품ID
        API ->> Product: 상품 정보 조회
        Product ->> Database: SELECT 상품 정보 (재고, 가격, 이름 등)
        Database -->> Product: 상품 메타데이터
        alt 상품이 DB에 존재
            Product -->> API: 상품 정보 반환
            API -->> User: 정상 응답 (상품 메타데이터)
        else 상품 미존재
            Product -->> API: 오류 (상품 없음)
            API -->> User: 오류 응답 ("상품이 존재하지 않음")
        end
    else 상품ID 미유효
        API -->> User: 오류 응답 ("유효하지 않은 상품ID")
    end
```

- **Product**가 상품 목록을 DB에서 조회하여 반환합니다.
- 사용자에게 상품명, 가격, 재고 등의 정보를 보여줍니다.
- 재고는 주문 시점에 다시 한 번 확인이 필요합니다.

<br>

## 쿠폰 발급

---
```mermaid
sequenceDiagram
    participant User
    participant API
    participant Coupon
    participant Database
    User ->> API: [POST] 쿠폰 발급 (couponId, userId)
    note over API: 1) 사용자 유효성 검증
    alt 유효한 사용자
        API ->> Coupon: 발급 처리
        note right of Coupon: 트랜잭션 시작<br>Lock(동시성 이슈 고려)
        Coupon ->> Database: 쿠폰 정보 SELECT (잔여 수량 등)
        Database -->> Coupon: 쿠폰 데이터
        alt 잔여 수량 > 0
            Coupon ->> Database: 이미 발급된 쿠폰인지 체크
            Database -->> Coupon: 중복 여부
            alt 중복 발급 아님
                Coupon ->> Database: 발급 이력 INSERT + 쿠폰 수량 1 감소
                Database -->> Coupon: 완료
                note right of Coupon: 트랜잭션 커밋
                Coupon -->> API: 발급 성공
                API -->> User: 정상 응답 (발급 완료)
            else 이미 발급된 쿠폰
                note right of Coupon: 트랜잭션 롤백
                Coupon -->> API: 오류 ("중복 발급 불가")
                API -->> User: 오류 응답
            end
        else 잔여 수량 0
            note right of Coupon: 트랜잭션 롤백
            Coupon -->> API: 오류 ("쿠폰 소진")
            API -->> User: 오류 응답
        end
    else 사용자 미유효
        API -->> User: 오류 응답 ("유효하지 않은 사용자")
    end

```

- **Coupon**에서 쿠폰 발급을 관리하며, 중복 발급 여부와 잔여 수량을 확인합니다.
- 발급 성공 시 쿠폰 발급 이력을 저장하고, 잔여 수량을 감소시킵니다.

## 쿠폰 조회

```mermaid
sequenceDiagram
    participant User
    participant API
    participant Coupon
    participant Database
    User ->> API: [GET] 쿠폰 조회 (userId)
    note over API: 사용자 유효성 검증
    alt 유효한 사용자
        API ->> Coupon: 사용자 쿠폰 목록 조회
        Coupon ->> Database: SELECT 쿠폰 발급 이력
        Database -->> Coupon: 쿠폰 목록
        Coupon -->> API: 쿠폰 목록
        API -->> User: 정상 응답 (보유 쿠폰 목록)
    else 미유효 사용자
        API -->> User: 오류 응답 ("유효하지 않은 사용자")
    end
```
- 사용자 ID로 쿠폰 테이블(또는 발급 이력 테이블)에서 보유 중인 쿠폰 목록을 조회합니다.
- 유효 기간이나 사용 여부 등은 추가 로직으로 확장 가능합니다.

<br>

## 주문-결제

---
```mermaid
sequenceDiagram
    participant User
    participant API
    participant Order
    participant Product
    participant Coupon
    participant Payment
    participant Database
    participant Analytics
    participant DataPlatform
    User ->> API: [POST] 주문-결제 (userId, productId, quantity, couponId 등)
    note over API: 1) 사용자 유효성 검증
    alt 유효한 사용자
        API ->> Order: 주문 생성 요청
        note over Order: 트랜잭션 시작<br>Lock(동시성 이슈 고려)
        Order ->> Product: 재고 조회
        Product ->> Database: SELECT 재고
        Database -->> Product: 재고 정보
        Product -->> Order: 재고 수량 결과
        alt 재고 부족
            note right of Order: 트랜잭션 롤백
            Order -->> API: 오류 ("재고 부족")
            API -->> User: 오류 응답
        else 재고 충분
            note over Order: 쿠폰 사용 여부 확인
            alt 쿠폰 사용
                Order ->> Coupon: 쿠폰 유효성 검증
                Coupon ->> Database: SELECT 쿠폰 상태
                Database -->> Coupon: 쿠폰 정보
                alt 쿠폰이 유효함
                    Coupon -->> Order: 사용 가능
                else 쿠폰 사용 불가
                    note right of Order: 트랜잭션 롤백
                    Order -->> API: 오류 ("쿠폰 사용 불가")
                    API -->> User: 오류 응답
                end
            end

            Order ->> Database: 주문 정보 INSERT
            Database -->> Order: 주문 ID
            Order ->> Payment: 결제 요청 (주문 ID, 결제금액 등)
            Note over Payment: [결제 상세 시퀀스 참조]

            alt 결제 성공
                Payment -->> Order: 결제 성공
                Order -->> API: 주문/결제 성공
                Order -->> Analytics: 주문/결제 성공
                Analytics ->> DataPlatform: 주문/결제 정보 전송
                note left of Order: 트랜잭션 커밋
                API -->> User: 정상 응답 (주문 완료)
            else 결제 실패
                Payment -->> Order: 결제 실패
                Order -->> API: 주문/결제 실패
                note left of Order: 트랜잭션 롤백
                API -->> User: 결제 실패 응답
            end
        end
    else 미유효 사용자
        API -->> User: 오류 응답 ("유효하지 않은 사용자")
    end

```

- **Order**가 트랜잭션을 관리하며 쿠폰 유효성 → 재고 확인 → 주문 생성 → 결제 로직을 순차적으로 진행합니다.
- 결제 로직은 **결제 상세** 시퀀스 참조로 분리하여 설명합니다.
- 최종적으로 재고 차감, 쿠폰 사용 처리까지 한 번에 처리한 뒤, **Analytics**으로 데이터 플랫폼으로 주문 정보를 전송할 수 있습니다.

<br>

## 결제 상세

```mermaid
sequenceDiagram
    participant Order
    participant Payment
    participant Balance
    participant Database
    Payment ->> Balance: 사용자 잔액 조회 (userId)
    note over Balance: Lock(잔액 동시성 이슈 고려)
    Balance ->> Database: SELECT 잔액
    Database -->> Balance: 잔액 정보
    Balance -->> Payment: 잔액 정보 응답

    alt 잔액 부족
        Balance -->> Payment: 잔액 부족
        Payment -->> Order: 결제 실패
    else 잔액 충분
        Payment ->> Balance: 잔액 차감 (userId, 결제금액)
        Balance ->> Database: UPDATE 잔액
        Database -->> Balance: 잔액 차감 완료
        Payment ->> Database: 결제 이력 INSERT
        Database -->> Payment: 결제 이력 생성 완료
        Balance -->> Payment: 차감 성공
        Payment -->> Order: 결제 성공
    end

```

- **Payment**에서 결제 로직을 처리합니다.
- 결제 전 **Balance**에서 잔액을 조회하여 잔액이 충분한지 확인합니다.
- 잔액이 부족하면 결제 실패, 충분하면 잔액을 차감합고 결제를 성공시킵니다.
- 결과를 **Order**로 반환합니다.

## 상위 상품 조회

---

```mermaid
sequenceDiagram
    participant User
    participant API
    participant Statistics
    participant Database
    User ->> API: [GET] 상위 상품 조회
    API ->> Statistics: 상위 상품 조회 요청
    note over Statistics: (캐시/Materialized View 고려)
    Statistics ->> Database: SELECT 최근 3일간 주문 데이터
    Database -->> Statistics: 주문 데이터
    note right of Statistics: 상품별 판매량 → 상위 5개 선별
    Statistics -->> API: 상위 5개 상품 목록
    API -->> User: 정상 응답 (Top 5 목록)
```

- **Statistics**(또는 다른 이름)가 최근 3일간 주문 테이블을 조회하여 상품별 판매량을 계산합니다.
- 상위 5개 상품을 선정해 사용자에게 반환합니다.
- 대량 트래픽이 예상될 시 Materialized View 나 비정규화된 특수 테이블을 고려해야합니다. 우선, 단순 SELECT 후 계산으로 가정합니다.
