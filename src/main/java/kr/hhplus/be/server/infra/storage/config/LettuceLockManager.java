package kr.hhplus.be.server.infra.storage.config;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockManager {

	private static final long EXPIRE_TIME_MILLIS = 5000L;
	private static final long WAIT_TIME_MILLIS = 5000L;
	private static final long RETRY_INTERVAL_MILLIS = 100L;
	private final StringRedisTemplate redisTemplate;

	public LettuceLockManager(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public String acquire(String lockKey) {

		String lockValue = UUID.randomUUID().toString();
		long deadline = System.currentTimeMillis() + WAIT_TIME_MILLIS;

		while (System.currentTimeMillis() < deadline) {
			Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, Duration.ofMillis(
				EXPIRE_TIME_MILLIS));
			if (Boolean.TRUE.equals(isLocked)) {
				return lockValue;
			}

			try {
				Thread.sleep(RETRY_INTERVAL_MILLIS); // 재시도 간격
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return null;
			}
		}

		return null;
	}

	public void release(String lockKey, String lockValue) {
		String currentValue = redisTemplate.opsForValue().get(lockKey);

		if (lockValue.equals(currentValue)) {
			redisTemplate.delete(lockKey);
		}
	}
}
