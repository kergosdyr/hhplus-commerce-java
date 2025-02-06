# 캐시란 무엇인가? 그리고 왜 캐시가 필요한가?

## 캐시라는 용어의 유래

캐시라는 단어는 프랑스어 cacher에서 유래되었으며, 원래 사냥꾼들이 사냥을 위한 장비나 도구들을 빠르게 사용하기 위해 근처에 묻어두거나 숨겨두는 장소를 의미하였습니다.
컴퓨터 과학에서는 이와 유사하게 자주 사용되거나 중요한 데이터를 임시로 저장하는 메모리 영역을 의미하며, 이를 통해 데이터베이스(DB)나 메인 메모리에 대한 접근 시간을 단축하여 시스템 성능을 높이는 역할을 합니다.

그럼 캐시는 왜 사냥꾼들의 은닉 도구라는 뜻에서 현재의 컴퓨터 과학을 위한 용어로 바뀌게 되었을까요? 처음에 등장했던 캐시는 캐시메모리를 의미하는 단어였습니다. 당시 매우 빠른 처리장치인 CPU 가 메인메모리나
하드디스크 등에 접근하면서 발생하는 속도 저하 문제를 해결하기 위해서 CPU 근처에 속도가 빠른 SRAM 을 배치하게 되었습니다.
이러한 방식이 성공을 이룬 이후에 SRAM 들은 캐시메모리라고 불리게 되었고 유사한 방식으로 근거리에서 데이터를 빠르게 제공하는 방법론들을 **캐싱** 이라고 부르기 시작했습니다.

## 캐시는 왜 필요한가?

최신 웹 어플리케이션에서는 모바일의 대대적인 성공 이후에 인터넷을 통한 트래픽이 급증하면서 캐싱의 중요성이 급부상하였습니다. 모든 웹의 요청을 데이터베이스로 송신하게 된다면 대대적인 장애로 이어질 수 있기
때문입니다.
또한, 응답시간은 이제 어플리케이션들의 주요한 매력 포인트중에 하나가 되었습니다. 더 이상 응답시간이 느린 어플리케이션들은 아무리 좋은 기능을 갖고 있다고 하더라도 소비자들에게 좋은 호응을 얻지 못합니다.

따라서 기업들은 장애를 미연에 방지하고, 어플리케이션들의 속도를 보장하기 위해서 캐시, 캐싱을 적극적으로 도입하고 있습니다.
하지만, 캐시가 모든걸 해결해주지는 못합니다. 캐시를 도입한다는 것은 실제 데이터를 저장하고있는 데이터베이스(혹은 다른 저장소) 와 같은 Key 값을 가진 데이터도 다른 값을 가질 수 있는 가능성을 내포하게 됩니다.
예를 들면 어떤 상품이 10개 판매되었지만, 캐시에는 8개만 판매된것으로 되어있을 수 있게 됩니다. 따라서 캐시를 사용하는 적절한 방법이 필요한데 이를 **캐시 전략**이라고 하고 적절한 캐시 전략을 선택하여
데이터의 정합성을 맞추는 것이 개발자에게 주요한 과제입니다.

# 캐시 전략

캐시 전략은 캐시를 적절하게 사용할 수 있는 방법론이며, 크게 **캐시 읽기 전략**, **캐시 쓰기 전략** 두가지로 나눠 볼 수 있습니다.

## 읽기를 위한 캐싱 전략

말 그대로 단순한 읽기를 위한 캐싱 전략입니다. 데이터를 읽어와서 빠르게 제공하는 것에 목적을 두고있습니다.

### Cache Aside(Look Aside)

어플리케이션이 직접 캐시를 제어하는 방법입니다.

1. 클라이언트가 데이터를 요청하면 어플리케이션이 먼저 캐시에서 조회합니다. 이때 조회가 된다면 즉시 반환합니다(Chace Hit)
2. 캐시에 데이터가 없으면(Cache Miss) DB에서 데이터를 읽어와 캐시에 저장한 후 반환합니다.

해당 전략은 동일한 결과 데이터를 보여주기 위한 작업에 사용되었을 때 효율적입니다.

예를 들어 e-커머스에서는 상위 상품 조회 시 해당 전략을 사용할 수 있습니다.

상위 상품 조회는 통계성 작업으로 24시간 동안 해당 데이터가 변하지 않기 때문에 캐시에서 데이터를 제공하는 경우에 성능상의 큰 이득을 얻을 수 있습니다.

> 다만, 데이터가 최초에 없거나 해당 데이터의 교체시점에 갑작스러운 트래픽이 몰리게 되는 경우 캐시를 조회하지 않고 데이터베이스 등 비용이 큰 작업을 동시에 수행하게 되어 부하가 발생하고 큰 장애로 이어질 수
> 있습니다.
> 이를 **Thundering Herd** 혹은 **Cache Stamped** 현상이라고 합니다.

### Read Through

Cache Aside(Look Aside) 와 유사하게 캐시에서 데이터를 읽어오는 전략입니다. 하지만 Cache Aside 와는 다르게 데이터베이스나 다른 저장소에서 읽어오는 것이 아니라 어플리케이션은 항상 캐시에만
의존하게 됩니다.
즉, 어플리케이션은 항상 캐시에서만 데이터를 조회하고, 데이터 동기화를 라이브러리나 다른 캐시 제공자등에게 위임하게 됩니다.
따라서, 캐시에는 항상 동기화된 데이터만 존재하게 되고 정합성을 맞추는 것이 훨씬 수월합니다. 하지만 캐시 서비스를 사용할 수 없게 되는 경우에 단일 장애 지점(SPOF) 가 될 수 있습니다.

1. 클라이언트가 데이터를 요청하면 어플리케이션은 먼저 캐시에서 조회합니다. 이때 조회가 된다면 즉시 반환합니다(Cache Hit)
2. 캐시에 데이터가 없으면 **캐시 혹은 관련 서비스** 에서 직접 데이터를 조회합니다. 어플리케이션은 데이터저장소를 직접 조회하지 않습니다.

e-커머스 시나리오에서는 동일하게 상위 상품 조회 시 해당 전략을 사용할 수 있습니다. 다만, 캐시를 조회하는 주체가 더이상 어플리케이션이 아니라 레디스 캐시 저장소가 되어야합니다.

## 쓰기를 위한 캐싱 전략

쓰기 전용 캐싱 전략은 데이터 업데이트 시 캐시와 데이터 저장소간의 일관성을 유지하면서도 애플리케이션의 성능을 극대화하기 위해 고안되었습니다.
실제 운영 환경에서는 데이터의 특성과 서비스 요구에 따라 다양한 접근 방식을 사용할 수 있으며,
대표적인 방법으로 Write Through, Write Around, 그리고 Write Back 전략이 있습니다.

### Write Through

Write Through 전략은 데이터가 변경될 때, 그 변경 내용을 캐시와 데이터 저장소에 함께 저장하는 방식입니다.
이로 인해 항상 캐시와 DB의 데이터가 동기화되어 있어, 데이터 일관성이 확실하게 보장됩니다.

1. 클라이언트가 데이터를 전송하면 어플리케이션은 캐시에 먼저 저장합니다
2. 캐시는 데이터 저장소로 해당 데이터를 저장합니다.

e-커머스 시나리오에서는 잔액의 조회, 상품의 정보 조회 등 자주 변하지 않는 메타 정보에 사용하면 효율적입니다.
만약 자주 변경되는 물품의 수량등에 적용하는 경우 한번의 요청에 여러번의 저장이 발생하게 되어 비효율적입니다.

### Write Around

Write Around 전략은 데이터를 변경할 때 캐시를 거치지 않고 DB에만 기록하는 방식입니다.
캐시에 불필요한 쓰기 작업이 발생하는 걸 막아서 효율성을 높일 수 있지만, 데이터 조회 시 캐시 미스가 발생할 확률이 높습니다.

1. 클라이언트가 데이터를 전송하면 어플리케이션은 데이터 저장소에 먼저 저장합니다.
2. 이후에 클라이언트가 데이터를 요청하는 경우, 데이터가 없다면 DB에 요청하여 데이터를 가져옵니다(Cache Aside 혹은 Read Through)

e-커머스 시나리오에서는 자주 변하지 않는 메타 데이터에 사용하면 효율적입니다.
자주 변경하는 데이터에 적용하는 경우 캐시 미스로 인해 잘못된 데이터를 조회할 확률이 높아져 문제의 소지가 있습니다.

### Write Back

Write Back 전략은 데이터를 동기화 하지 않고 나중에 저장하는 방식입니다. 데이터를 저장할 때 데이터 저장소에 직접 저장하는 것이 아니라 캐시에 저장합니다.
일정 주기 이후에 캐시에 있는 데이터를 데이터 저장소에 반영함으로써 부하가 발생할 수 도 있는 작업들을 줄일 수 있습니다.
쓰기 작업이 빈번하면서 읽어오는데에 많은 양의 리소스가 발생하는 작업에 적합합니다.

1. 클라이언트는 어플리케이션에 요청을 보내고, 이는 데이터 저장소에 바로 저장되지 않고 캐시에 저장됩니다.
2. 일정 주기 이후에 캐시에 저장된 정보를 데이터 저장소로 보내 저장합니다.

e-커머스 시나리오에서 Write Back 전략은 상위 상품 조회에 효율적일 수 있습니다.
예를 들면 물품을 구매하면 즉시 해당 로그 혹은 수치를 캐시에 저장하고, 이를 하루 혹은 시간 별로 데이터저장소에 반영하여 부하 발생을 낮출 수 있습니다.

# **Thundering Herd** 혹은 **Cache Stampede** 현상이란?

Thundering Herd (혹은 Cache Stampede) 현상은 캐시 시스템에서 주로 발생하는 문제로,
특정 캐시 항목이 만료되거나 캐시에 존재하지 않을 때 다수의 클라이언트 혹은 프로세스가 동시에 데이터 저장소에 접근하여 동일한 데이터를 요청하는 상황을 의미합니다.

1. 캐시 만료 또는 미스 상황 : 캐시 항목의 유효 기간이 만료되거나, 아직 캐시에 로드되지 않은 상태에서 다수의 요청이 동시에 발생하면 모든 요청이 캐시 미스를 경험하게 됩니다.
2. 동시 요청 증가 : 캐시 미스로 인해 각 요청이 백엔드 데이터 저장소에 접근하게 되며, 이로 인해 일시적으로 데이터베이스에 과도한 부하가 발생할 수 있습니다.
3. 부하 집중 : 백엔드 서비스는 갑작스런 폭증하는 요청을 처리해야 하므로, 서비스 성능 저하나 심한 경우 장애까지 초래할 수 있습니다.

e-커머스에서는 상위 상품 조회가 트래픽이 캐시가 초기화 되는 시점에 몰리는 경우 동시에 많은 요청이 DB로 들어가기 때문에 문제가 발생할 수 있습니다.

## **Thundering Herd** 혹은 **Cache Stampede** 용어의 유래 및 무엇이 맞는 용어인가?

### Thundering Herd

해당 용어는 최초에 운영체제와 동시성 제어 분야에서 유래된 단어입니다. 다수의 프로세스나 스레드가 하나의 이벤트에 동시에 깨워지는 현상을 동물들이 몰려도는 현상에 비유하여 표현한 단어입니다.
즉, 시스템 자원을 과도하게 경쟁하게 만들어서 성능 저하를 초래하는 상황을 지칭합니다.

### Cache Stampede

해당 용어는 캐시 시스템에 좀더 특화된 맥락에서 사용되는 용어로, 캐시 항목이 만료되어 다수의 클라이언트가 동시에 데이터 저장소에 접근하여 데이터를 새로 로드할 때 발생하는 문제를 의미합니다.

### 어떤 용어를 써야하는가?

현재 시스템의 상황을 미루어 보았을 때 캐시 시스템에서 캐시가 만료됨으로 인해서 다수의 클라이언트가 동시에 데이터 저장소를 접근하여 로드할 때 발생하는 문제를 의미하기 때문에
Cache Stampede 라고 지칭하는것이 조금 더 옳은 용래인것으로 보이나, Thundering Herd 문제라고 하더라도 이해에 큰 문제는 없습니다.

## 해결 방법들

### Cache Warming

캐시 워밍은 예상되는 요청 전에 미리 캐시에 데이터를 적재해두어, 캐시 만료 시점에 다수의 요청이 동시에 DB에 접근하는 것을 방지하는 방법입니다. Spring에서는 @Scheduled 애너테이션을 활용해 주기적으로
캐시를 갱신할 수 있습니다.

> 예시 코드

```java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheWarmingService {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private DataService dataService; // DB 혹은 원본 데이터 접근 서비스

	// 예를 들어 "productCache"에 저장된 모든 상품 데이터를 캐시에 사전 로딩
	@Scheduled(fixedDelayString = "PT23H30M") // 23시간 30분마다 실행 (24시간 TTL에서 만료 전에 미리 갱신) 
	public void warmUpProductCache() {
		Cache productCache = cacheManager.getCache("productCache");
		if (productCache == null) {
			return;
		}
		// DB 혹은 외부 API로부터 모든 상품 데이터를 조회
		List<Product> productList = dataService.findAllProducts();
		for (Product product : productList) {
			productCache.put(product.getId(), product);
		}
		System.out.println("Product cache warmed up successfully.");
	}
}
```

### PER 알고리즘

PER 알고리즘은 캐시 만료 전에 확률적으로 미리 캐시 값을 재계산하여, 동시에 여러 요청이 캐시 만료와 함께 DB에 접근하는 상황을 완화합니다.
남은 TTL에 기반해 일정 확률로 캐시를 갱신하도록 구현할 수 있습니다.

> 예시 코드

```java
public Data getDataWithPER(String key, long ttl) {
	// 캐시에서 데이터와 저장 시각 정보를 포함한 값을 조회
	CacheValue cacheValue = cache.get(key);

	// 캐시가 없거나 TTL이 만료된 경우
	if (cacheValue == null || cacheValue.isExpired()) {
		Data freshData = dataService.loadData(key);
		cache.put(key, new CacheValue(freshData, ttl));
		return freshData;
	}

	// 남은 TTL에 따른 갱신 확률 계산
	long remaining = cacheValue.getRemainingTTL(); // 예: 남은 TTL (ms)
	double probability = 1.0 - ((double)remaining / ttl);

	if (Math.random() < probability) {
		// 확률적으로 DB에서 새 데이터를 로드 후 캐시 갱신
		Data freshData = dataService.loadData(key);
		cache.put(key, new CacheValue(freshData, ttl));
		return freshData;
	}

	return cacheValue.getData();
}
```

### Mutex Lock

Mutex Lock은 동시에 하나의 요청만 데이터 소스(DB 등)에 접근하여 캐시를 업데이트하도록 제어하는 방법입니다.
분산 락을 사용하여 캐시가 비워졌을 때 하나의 요청만 캐시를 업데이트 하도록 구현하는 방식입니다.

> 예시 코드

```java
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MutexCacheService {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private DataService dataService;

	@Autowired
	private RedissonClient redissonClient;

	public <T> T getDataWithMutex(String key, long ttlMillis, Class<T> type) {
		Cache cache = cacheManager.getCache("dataCache");
		Cache.ValueWrapper wrapper = cache.get(key);
		// 캐시 미스 발생 -> Mutex Lock 적용
		String lockKey = "lock:" + key;
		RLock lock = redissonClient.getLock(lockKey);
		// 최대 10초 동안 락 시도, 락 획득 후 5초 유지
		if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
			// 다른 스레드가 먼저 캐시를 갱신했는지 다시 확인
			wrapper = cache.get(key);
			if (wrapper != null && wrapper.get() != null) {
				return (T)wrapper.get();
			}
			// DB 혹은 원본 데이터 소스로부터 데이터 로드
			T freshData = dataService.loadData(key);
			cache.put(key, freshData); // 캐시에 저장 (Spring CacheManager의 TTL 설정을 별도로 적용)
			return freshData;
		}
	}
}
```

# 결론

e-커머스 시나리오에 상위 상품 조회에 읽기를 위한 캐시 전략을 적용하는 것이 가장 효율적으로 파악되었습니다.
사유는 복잡한 쿼리로 인해 읽기의 성능이 떨어지는 것에 비해 쓰는 행위는 24시간 마다 한번씩 발생하는 통계성 쿼리이기 때문에 이를 Cache Aside 혹은 Read Through 를 사용하는 경우 큰 효용성을
얻을 수 있는데
이때 여기서는 Cache Aside 를 사용하기로 결정했습니다.

Read Through 는 어플리케이션이 아닌 현재 캐시(Redis) 에서 직접 데이터 저장소(DB) 에 동기화를 해줘야 하고 레디스가 단일 장애지점이 될수도 있기 때문에
**Cache Aside** 를 적용하는 것이 좀더 옳은 판단이라고 생각했습니다.

이때, Cache Stampede 현상 방지를 위한 몇가지 방법이 필요한데 이중에 저는 Cache Warming 전략을 선택하였습니다. 왜냐면 PER 등의 알고리즘을 통해 확률적으로 접근하도록 하는것이 굳이 불필요한
도메인이라고 판단하였습니다.
상위 상품은 특정 시간에 조회되는 것이기 때문에 해당 시간을 맞추어서 캐시의 TTL 을 걸고, 그전에 Cache Warming 을 통해서 데이터를 들고온다면 정확한 데이터를 개발자의 컨트롤하에 가져올 수 있지만
PER 등의 확률로 가져오는 경우 데이터를 가져오는 시간을 정확히 컨트롤 할 수 없기 때문에 다수의 사람들이 갱신되지 않은 정보를 볼 확률도 높아질 뿐더러 갱신되었지만 예전의 데이터를 들고 올 수 있는 확률도
있습니다.

Mutex Lock 의 경우에는 가장 확실한 방법일 수 있지만 상위 상품의 데이터들은 이전에 캐싱된 데이터가 있다면 그 데이터를 보여줘도 크게 무방하지 않다는 해당 도메인이 가진 특성상 굳이 Lock 을 걸어서
성능을 저하시킬 필요는 없다고 판단했습니다.


