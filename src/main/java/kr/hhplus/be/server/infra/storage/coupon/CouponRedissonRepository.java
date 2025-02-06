package kr.hhplus.be.server.infra.storage.coupon;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.enums.RedisKeyPrefix;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponRedissonRepository {

	private final RedissonClient redissonClient;

	public long issue(long couponId) {

		String key = RedisKeyPrefix.COUPON.getKey(couponId);

		RAtomicLong couponCounter = redissonClient.getAtomicLong(key);
		long currentCouponAmount = couponCounter.get();

		if (currentCouponAmount <= 0) {
			throw new ApiException(ErrorType.COUPON_NOT_ISSUABLE);
		}

		return couponCounter.decrementAndGet();
	}
}
