# **동시성 문제 해결을 위한 접근 및 테스트 보고서**

## 1. **배경 (Background)**

현대의 e-커머스 환경에서는 수많은 사용자가 동시에 상품을 조회하거나 구매를 시도하는 상황이 자주 발생합니다. 이러한 다중 접속 환경에서 동일 자원(쿠폰, 재고, 잔고 등)에 대해 동시에 접근하면, 데이터 불일치(
oversell, 중복 사용 등) 문제가 발생할 수 있습니다. 이를 동시성 문제(Concurrency Issue)라 하며, 해결하지 못하면 서비스 신뢰도와 매출 손실에 직접적인 영향을 끼칠 수 있습니다.

> 동시성 문제의 개념적 예시  
> ![img.png](img.png)

---

## 2. **e-커머스 시나리오에서의 동시성 문제 분석**

e-커머스 시나리오에서 발생하는 전형적인 동시성 이슈는 다음과 같습니다.

1. **쿠폰 발행 시 동시성 문제**
    - 예: 쿠폰이 1개 남았지만 동시에 2명 이상이 발행 요청 → 발행 수를 초과하여 쿠폰이 지급될 수 있음.

2. **쿠폰 사용 시 동시성 문제**
    - 예: 동일 쿠폰을 동시에 2명 이상이 사용하는 상황 → 실제론 1명만 사용 가능해야 하지만 2명이 모두 사용 성공.

3. **재고 차감 시 동시성 문제**
    - 예: 재고가 1개뿐인데 동시에 여러 구매 요청이 발생 → 재고가 음수가 되는 oversell 현상 발생.

4. **잔고 사용·추가 시 동시성 문제**
    - 예: 잔고 1000원일 때 동시에 2건(각 1000원 사용) 요청 → 실제론 1건만 가능해야 하나 2건 모두 성공할 수 있음.
    - 예: 잔고를 추가하는 경우도 동시에 여러 건 요청이 들어오면 1건만 반영되거나, add와 subtract가 겹치는 경우 오작동 가능.

위 문제들은 트랜잭션 처리 순서가 미묘하게 어긋나거나, “확인 - 실행” 사이 타이밍 차이로 인해 발생합니다.  
**따라서, 적절한 동시성 제어(트랜잭션, 락, 분산 락 등)가 필수적**입니다.

---

## 3. **현재 시스템의 동시성 이슈 현황**

현재 시스템에서 동시성 문제가 언제든 발생할 수 있는 대표 지점은 다음과 같습니다.

- **쿠폰 발행/사용**: 쿠폰 수량이 적을 때 동시 발행·사용 요청 시 초과 발행/초과 사용
- **재고 차감**: 상품 재고가 충분치 않을 때, 동시에 구매 API 호출이 다수 발생
- **잔고 변경**: 잔고 변경(차감/추가)이 동시에 일어나는 경우 중복 또는 반영 누락

DB 차원에서 Isolation Level만 신경 썼거나, 애플리케이션 차원에서 락을 충분히 고려하지 않은 상태라면, 서비스 운영 중 예상치 못한 데이터 불일치가 발생할 수 있습니다.

---

## 4. **현재 시스템에서 적용가능한 동시성 제어 기법**

동시성 문제를 해결하기 위한 접근 방식은 크게 **DB 트랜잭션**(Isolation Level, JPA 락, Named Lock 등)과 **Redis 분산 락**(Lettuce, Redisson 등)으로 나눌 수
있습니다.

### 4.1 DB 트랜잭션 기반 동시성 제어

1. **트랜잭션 격리 수준(Isolation Level)**
    - READ UNCOMMITTED, READ COMMITTED, REPEATABLE READ, SERIALIZABLE
    - 각 수준별로 Dirty Read, Non-Repeatable Read, Phantom Read 발생 여부가 달라집니다.
    - MySQL 기본 격리 수준은 REPEATABLE READ 입니다.

2. **낙관적 락(Optimistic Lock)**
    - `@Version` 사용, 수정 시점에 버전 불일치 시 예외 발생 후 재시도
    - 충돌이 드문 환경에서 쓰기 성능이 좋지만, 충돌 발생 시 재시도 로직이 필요

3. **비관적 락(Pessimistic Lock)**
    - 데이터 수정/조회 전에 DB 락을 획득해 충돌을 사전에 차단
    - 충돌은 확실히 방지하지만, DB 락으로 인한 데드락, 성능 저하 위험 증가

4. **Named Lock (MySQL GET_LOCK / RELEASE_LOCK)**
    - 특정 리소스를 이름(`lockName`)으로 잠그는 방식
    - 분산 환경에도 어느 정도 대응 가능하지만, 락 해제 관리가 필요

### 4.2 Redis 분산 락

1. **Lettuce 기반 Spin Lock**
    - `setIfAbsent(SETNX)` + TTL로 락 구현
    - 락을 얻을 때까지 반복 시도(스핀) → 네트워크 I/O 부담, p95 응답시간 증가
    - 구현 예시:
      ```java
      @Component
      @RequiredArgsConstructor
      public class RedisLockRepository {
          private final RedisTemplate<String, String> redisTemplate;
          
          public Boolean lock(Long key) {
              return redisTemplate
                  .opsForValue()
                  .setIfAbsent("lock:"+key, "lock", Duration.ofMillis(1000));
          }
          
          public Boolean unlock(Long key) {
              return redisTemplate.delete("lock:"+key);
          }
      }
      
      @Component
      @Slf4j
      @RequiredArgsConstructor
      public class LettuceLockService {
          private final RedisLockRepository redisLockRepository;
          
          public void businessLogic(long key) {
              try {
                  while (!redisLockRepository.lock(key)) {
                      // waiting (스핀)
                  }
                  // 비즈니스 로직
              } finally {
                  redisLockRepository.unlock(key);
              }
          }
      }
      ```

2. **Redisson 기반 Pub-Sub 락**
    - Pub-Sub를 통해 락 해제 이벤트를 전달 → 스핀 없이 이벤트 기반으로 대기
    - 락 재시도, 자동 해제 등 고수준 API 제공
    - Lettuce Spin Lock 대비 부하가 낮고 확장성이 높음
    - 구현 예시:
      ```java
      @Component
      @Slf4j
      @RequiredArgsConstructor
      public class RedissonLockService {
          private final RedissonClient redissonClient;
 
          public void businessLogic(long key) throws InterruptedException {
              RLock lock = redissonClient.getLock(String.valueOf(key));
              if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                  try {
                      // 비즈니스 로직
                  } finally {
                      lock.unlock();
                  }
              } else {
                  // 락 획득 실패 시 로직
              }
          }
      }
      ```

---

## 5. **가설**

본 보고서에서는 **DB 비관적 락**, **Redis-Lettuce(Spin Lock)**, **Redis-Redisson(Pub-Sub)** 방식을 실제로 구현하여 부하 테스트를 진행하였습니다.  
이때, **세 가지 방식의 성능(응답 시간) 및 안정성**에 대해 다음과 같은 가설을 세웠습니다

- **가설 1 (DB 비관적 Lock)**
  > “현재 잡은 커넥션 풀 사이즈가 그리 크지 않고, 테스트 요청도 동시에 300개 정도로 크게 잡지 않았으므로, **DB 비관적 락이 준수한 성능**을 보여줄 것이다.”

- **가설 2 (Redis-Lettuce Spin Lock)**
  > “코드에서 **retry interval**을 일정 시간 두었기 때문에, 락 획득을 위한 스핀 동작으로 **성능적으로 가장 좋지 않을 것**이다.”

- **가설 3 (Redis-Redisson Pub-Sub)**
  > “Pub-Sub 방식을 사용하기 때문에 Lettuce Spin Lock보다 **더 높은 성능**을 낼 것이고, 세 가지 방식 중 **성능상으로도 가장 좋을 것**이다.”

---

## 6. **테스트 환경 구성**

- **테스트 도구**: K6
- **테스트 시나리오**:
    1) 300명의 사용자가 동시에 “쿠폰 발급” API 요청
    2) 모든 발급이 끝난 뒤 “주문(재고 차감)” API를 요청

    - 각 요청은 0.1초 간격으로 순차(동시 300명) 발생 (총 600회의 호출)

- **테스트 환경**:
    - **DB**: MySQL 8.x
    - **Redis**: 단일 노드 6.x
    - **서버**: Spring Boot 애플리케이션
    - **측정 도구**: K6

- **측정 지표**:
    - **평균 응답 시간** (mean)
    - **p95 응답 시간** (95th percentile)

---

## 7. **테스트 결과**

| 동시성 제어 방식                     | 평균 응답 시간 | p95 응답 시간 |
|-------------------------------|----------|-----------|
| **DB 비관적 락**                  | 약 2.39s  | 약 6.56s   |
| **Redis-Lettuce** (Spin Lock) | 약 5.21s  | 약 20.99s  |
| **Redis-Redisson** (Pub-Sub)  | 약 4.15s  | 약 11.36s  |

1. **DB 비관적 락**
    - 커넥션 풀이 충분한 상황에서 평균 응답 시간이 **2.39s**로 가장 빠름
    - p95도 **6.56s**로 비교적 안정적
    - **가설 1**에 부합: “DB 락이 준수한 성능을 낼 것”이라는 예상과 일치

2. **Redis-Lettuce (Spin Lock)**
    - 평균 응답 시간이 **5.21s**, p95가 **20.99s**로 세 가지 중 가장 저조
    - 스핀 락 특성상 락 해제를 기다리며 반복 시도 → 응답 지연이 큼
    - **가설 2**에 부합: “스핀 락으로 인해 가장 좋지 않은 성능”이라는 예상과 일치

3. **Redis-Redisson (Pub-Sub)**
    - 평균 **4.15s**, p95 **11.36s**로 Lettuce보다 개선
    - DB 락보다는 느리지만, 분산 환경에서는 훨씬 유리
    - **가설 3**에 부합: “Redisson이 Lettuce보다 더 나은 성능” 확인

---

## 8. **선택 및 고찰**

- **DB 비관적 락**
    - 현재 테스트 환경(동접 300, 커넥션 풀 50)에서는 가장 낮은 응답 시간
    - 그러나 대규모 트래픽·분산 서버 환경에서 **데드락, 커넥션 풀 고갈** 위험이 커질 수 있음

- **Redis-Lettuce Spin Lock**
    - 구현이 간단하지만, 스핀 락 구조로 **p95 지연**이 가장 높게 나타남
    - 대기 중에도 Redis에 지속적으로 Lock 요청을 보내기 때문에, 트래픽 증가 시 부하가 심각해질 수 있음

- **Redis-Redisson Pub-Sub**
    - 스핀 락 문제가 어느 정도 해결되어 Lettuce 대비 성능과 안정성이 좋음
    - DB 락보다 절대 시간은 다소 느렸지만, **분산 환경**(멀티 서버, 마이크로서비스 등)에서 유리
    - Redis 자체가 싱글 포인트가 될 수 있지만, **클러스터링**(Sentinel, Cluster)으로 보완 가능

---

## 9. **결론**

테스트 결과, **현재 규모(동접 300)** 기준으로:

1. **DB 비관적 락**이 평균/최대 응답 시간에서 우수 → **가설 1** 충족
2. **Redis-Lettuce(Spin Lock)**이 가장 느린 응답 시간 → **가설 2** 충족
3. **Redis-Redisson(Pub-Sub)**이 Lettuce보다 빠른 성능 → **가설 3** 충족

**따라서**, 소규모 트래픽에는 DB 락이 빠를 수 있으나,  
**대규모 트래픽**이나 **분산 서버**(멀티 인스턴스) 환경을 고려한다면, **Redisson**이 확장성과 안정성을 유지하며 적절한 성능을 내는 **균형 잡힌 선택**이라 판단됩니다.

---

> **최종 요약**
> - **DB 락**은 적은 트래픽 환경에서 우수한 성능을 보이나, 대규모 트래픽·분산 서버에서 데드락 위험이 커짐
> - **Lettuce Spin Lock**은 간단하지만 p95 응답 시간이 매우 길어질 수 있음
> - **Redisson Pub-Sub**는 분산 환경에서 안정적이며 Lettuce 대비 성능이 우수
> - 실제 적용 시에는 서비스 트래픽, 인프라 구성, 장애 가능성 등을 종합 고려해야 함

## **부록**

### A. **코드 예시**

- **Named Lock** 구현 예:
  ```java
  public interface NamedLockRepository extends JpaRepository<..., ...> {
      @Query(value = "SELECT GET_LOCK(:lockName, 10)", nativeQuery = true)
      int acquireLock(@Param("lockName") String lockName);

      @Query(value = "SELECT RELEASE_LOCK(:lockName)", nativeQuery = true)
      int releaseLock(@Param("lockName") String lockName);
  }
  ```

- **JPA 낙관적 락 엔티티**:
  ```java
  @Entity
  public class Coupon {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      private Integer remainCount;

      @Version
      private Long version;
      
      // getter, setter ...
  }
  ```

