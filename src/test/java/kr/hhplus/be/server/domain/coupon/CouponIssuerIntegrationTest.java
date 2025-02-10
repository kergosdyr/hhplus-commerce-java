package kr.hhplus.be.server.domain.coupon;

import static kr.hhplus.be.server.config.TestUtil.createTestCoupon;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLong;

import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.error.ApiException;

class CouponIssuerIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("만료된 쿠폰을 발행하려고 하면 ApiException(COUPON_NOT_ISSUABLE)이 발생한다")
	void shouldThrowApiExceptionWhenCouponIsAlreadyExpired() {
		//given
		Coupon coupon = createTestCoupon(LocalDateTime.of(2024, 12, 31, 0, 0));
		Coupon savedCoupon = couponJpaRepository.save(coupon);

		//when && then
		assertThatThrownBy(() -> {
			couponIssuer.issue(1L, savedCoupon.getCouponId(), LocalDateTime.of(2025, 1, 1, 0, 0));
		}).isInstanceOf(ApiException.class).hasMessageContaining("쿠폰이 만료되었거나 모두 소진되었습니다");

	}

	@Test
	@DisplayName("이미 발급된 쿠폰을 발행하려고 하면 ApiException(COUPON_ALREADY_ISSUED)이 발생한다")
	void shouldThrowApiExceptionWhenCouponIsAlreadyIssued() {
		//given
		Coupon coupon = createTestCoupon(LocalDateTime.of(2024, 1, 2, 0, 0));
		Coupon savedCoupon = couponJpaRepository.save(coupon);

		UserCoupon userCoupon = TestUtil.createTestUserCoupon(1L, savedCoupon.getCouponId());

		UserCoupon savedUserCoupon = userCouponJpaRepository.save(userCoupon);

		//when && then
		assertThatThrownBy(() -> {
			couponIssuer.issue(1L, savedCoupon.getCouponId(), LocalDateTime.of(2025, 1, 1, 0, 0));
		}).isInstanceOf(ApiException.class).hasMessageContaining("쿠폰이 만료되었거나 모두 소진되었습니다.");
	}

	@Test
	@DisplayName("존재하지 않는 쿠폰을 발행하려고 하면 ApiException(COUPON_NOT_FOUND)이 발생한다")
	void shouldThrowApiExceptionWhenCouponIsNotFound() {
		//given
		Coupon coupon = createTestCoupon(LocalDateTime.of(2024, 1, 2, 0, 0));
		Coupon savedCoupon = couponJpaRepository.save(coupon);

		//when && then
		assertThatThrownBy(() -> {
			couponIssuer.issue(1L, 999L, LocalDateTime.of(2025, 1, 1, 0, 0));
		}).isInstanceOf(ApiException.class).hasMessageContaining("요청하신 쿠폰을 찾을 수 없습니다");

	}

	@Test
	@DisplayName("정상적으로 호출되고 스케쥴러가 호출되면 쿠폰이 정상적으로 감소한다.")
	void shouldIssueCouponAndReduceQuantityWhenCalledSuccessfully() {
		//given

		Coupon coupon = createTestCoupon(LocalDateTime.now().plusDays(1L));
		Coupon savedCoupon = couponJpaRepository.save(coupon);
		RAtomicLong couponCounter = TestUtil.addAndGetCouponCount(redissonClient, savedCoupon.getCouponId(), 10L);
		long beforeCouponCounter = couponCounter.get();

		//when
		UserCoupon issuedUserCoupon = couponIssuer.issue(1L, savedCoupon.getCouponId(),
			LocalDateTime.of(2025, 1, 1, 0, 0));
		Coupon updatedCoupon = couponJpaRepository.findById(savedCoupon.getCouponId())
			.orElseThrow(RuntimeException::new);
		userCouponIssueScheduler.issueAllCouponWait();

		//then
		assertThat(issuedUserCoupon.getCouponId()).isEqualTo(updatedCoupon.getCouponId());
		assertThat(issuedUserCoupon.getAmount()).isEqualTo(updatedCoupon.getAmount());
		assertThat(beforeCouponCounter).isEqualTo(couponCounter.get() + 1);

	}

}