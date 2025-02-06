package kr.hhplus.be.server.infra.redis.lock;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.support.LockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedissonLockManager implements LockManager {

	private static final String LOCK_PREFIX = "redisson:";
	private static final long EXPIRE_TIME_SECONDS = 100L;
	private static final long WAIT_TIME_SECONDS = 50L;

	private final RedissonClient redissonClient;

	private String getKey(String lockKey) {
		return LOCK_PREFIX + lockKey;
	}

	@Override
	public String acquire(String lockKey) {
		RLock rLock = redissonClient.getLock(getKey(lockKey));
		try {
			boolean isLocked = rLock.tryLock(WAIT_TIME_SECONDS, EXPIRE_TIME_SECONDS, TimeUnit.SECONDS);
			if (isLocked) {
				log.info("Lock acquired: {}", getKey(lockKey));
				return rLock.getName(); // 락 이름 반환
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Failed to acquire lock: {}", getKey(lockKey), e);
		}
		return null;
	}

	@Override
	public List<String> acquireAll(List<String> lockKeys) {
		List<RLock> locks = lockKeys.stream()
			.map(this::getKey)
			.map(redissonClient::getLock)
			.toList();
		RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));
		try {
			boolean isLocked = multiLock.tryLock(WAIT_TIME_SECONDS, EXPIRE_TIME_SECONDS, TimeUnit.SECONDS);
			if (isLocked) {
				log.info("MultiLock acquired for keys: {}", lockKeys);
				return lockKeys;
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Failed to acquire multi-lock for keys: {}", lockKeys, e);
		}
		return List.of();
	}

	@Override
	public void release(String lockKey) {
		RLock rLock = redissonClient.getLock(getKey(lockKey));
		if (rLock.isHeldByCurrentThread()) {
			rLock.unlock();
			log.info("Lock released: {}", getKey(lockKey));
		}
	}

	@Override
	public void releaseAll(List<String> lockKeys) {
		List<RLock> locks = lockKeys.stream()
			.map(this::getKey)
			.map(redissonClient::getLock)
			.toList();
		RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));
		try {
			multiLock.unlock();
			log.info("MultiLock released for keys: {}", lockKeys);
		} catch (IllegalMonitorStateException e) {
			log.error("Failed to release multi-lock for keys: {}", lockKeys, e);
		}
	}
}
