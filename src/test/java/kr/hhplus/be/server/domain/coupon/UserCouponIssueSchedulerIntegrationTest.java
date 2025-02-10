package kr.hhplus.be.server.domain.coupon;

import static kr.hhplus.be.server.config.TestUtil.createTestCoupon;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLong;

import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.enums.RedisKeyPrefix;

class UserCouponIssueSchedulerIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("UserCouponIssueScheluder 는 대기열의 수 만큼 UserCoupon 을 발급한다.")
	void shouldIssueCouponWhenHasWaitList() {
		//given
		Coupon coupon = createTestCoupon(LocalDateTime.of(2025, 12, 31, 0, 0));
		Coupon savedCoupon = couponJpaRepository.save(coupon);
		RAtomicLong couponCounter = addCouponCount(savedCoupon.getCouponId(), 30L);

		IntStream.range(0, 10)
			.forEach(i -> userCouponRedissonRepository.publishCouponIssuedWait(i, coupon.getCouponId(),
				LocalDateTime.of(2025, 1, 1, 0, 0)));

		//when
		userCouponIssueScheduler.issueAllCouponWait();

		//then
		List<UserCoupon> userCoupons = userCouponJpaRepository.findAll();
		assertThat(userCoupons.size()).isEqualTo(10);
		assertThat(couponCounter.get()).isEqualTo(20);

	}

	private RAtomicLong addCouponCount(long couponId, long count) {
		RAtomicLong couponCounter = redissonClient.getAtomicLong(RedisKeyPrefix.COUPON.getKey(couponId));
		couponCounter.addAndGet(count);
		return couponCounter;
	}

}