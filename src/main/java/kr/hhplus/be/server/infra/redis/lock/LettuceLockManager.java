package kr.hhplus.be.server.infra.redis.lock;

import java.time.Duration;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.config.LockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("lettuce")
public class LettuceLockManager implements LockManager {

	private static final long EXPIRE_TIME_MILLIS = 100000L;
	private static final long WAIT_TIME_MILLIS = 50000L;
	private static final long RETRY_INTERVAL_MILLIS = 100L;
	private final StringRedisTemplate redisTemplate;

	@Override
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

	@Override
	public void release(String lockKey) {
		String lockValue = redisTemplate.opsForValue().get(lockKey);
		if (lockValue != null) {
			redisTemplate.delete(lockKey);
		}
	}
}