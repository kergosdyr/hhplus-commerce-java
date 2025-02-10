package kr.hhplus.be.server.domain.coupon;

import static kr.hhplus.be.server.config.TestUtil.createTestCoupon;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLong;

import kr.hhplus.be.server.config.ConcurrencyTestUtil;
import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.enums.RedisKeyPrefix;

class CouponIssuerConcurrencyTest extends IntegrationTest {

	@Test
	@DisplayName("30개의 쿠폰이 존재하고, 40번의 쿠폰 동시 요청이 있을 경우 issue 는 30번만 성공해야한다.")
	void shouldIssueOnly30CouponsWhen40ConcurrentRequestsAreMade() throws InterruptedException {
		//given
		Coupon coupon = createTestCoupon(LocalDateTime.now().plusDays(1L));
		Coupon savedCoupon = couponJpaRepository.save(coupon);
		RAtomicLong couponCount = addCouponCount(savedCoupon.getCouponId(), 30L);
		//when
		AtomicLong userId = new AtomicLong();
		var run = ConcurrencyTestUtil.run(100, () -> {
			try {
				couponIssuer.issue(userId.getAndIncrement(), savedCoupon.getCouponId(),
					LocalDateTime.of(2025, 1, 1, 0, 0));
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		});
		//then
		assertThat(run.success()).isEqualTo(100);
		userCouponIssueScheduler.issueAllCouponWait();
		List<UserCoupon> issuedAllUserCoupon = userCouponJpaRepository.findAll();
		assertThat(issuedAllUserCoupon).hasSize(30);

	}

	private RAtomicLong addCouponCount(long couponId, long count) {
		RAtomicLong couponCounter = redissonClient.getAtomicLong(RedisKeyPrefix.COUPON.getKey(couponId));
		couponCounter.addAndGet(count);
		return couponCounter;
	}

}