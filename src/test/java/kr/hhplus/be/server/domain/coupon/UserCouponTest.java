package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.enums.UserCouponStatus;

class UserCouponTest {

	@Test
	@DisplayName("UserCoupon 의 use 를 사용하면, 쿠폰의 상태를 USED 로 변경하고, isUsed 를 true 로 만들고 주어진 totalPrice 에 대해서 쿠폰의 amount 만큼 차감한다")
	void shouldCouponStatusChangedUsedIsUsedTrueTotalPriceAmonutDeduct() {
		//given
		long totalPrice = 10000L;
		UserCoupon mockUserCoupon = TestUtil.createTestUserCoupon(1L, 1L);

		assertThat(mockUserCoupon.isUsed()).isFalse();
		assertThat(mockUserCoupon.getStatus()).isEqualTo(UserCouponStatus.AVAILABLE);

		//when
		long couponUsedPrice = mockUserCoupon.use(totalPrice);

		//then
		assertThat(couponUsedPrice).isEqualTo(9000L);
		assertThat(mockUserCoupon.isUsed()).isTrue();
		assertThat(mockUserCoupon.getStatus()).isEqualTo(UserCouponStatus.USED);
	}

	@Test
	@DisplayName("UserCoupon 의 fromCoupon 을 사용하면 주어진 값에 의해 UserCoupon 을 생성한다")
	void shouldCreateUserCouponWhenUsingFromCoupon() {
		//given
		Coupon testCoupon = TestUtil.createTestCoupon(LocalDateTime.of(2025, 1, 1, 0, 0));
		UserCoupon userCoupon = UserCoupon.fromCoupon(testCoupon, 1L,
			LocalDateTime.of(2025, 1, 1, 0, 0));

		//when & then
		assertThat(userCoupon.getCouponId()).isEqualTo(testCoupon.getCouponId());
		assertThat(userCoupon.getStatus()).isEqualTo(UserCouponStatus.AVAILABLE);
		assertThat(userCoupon.getAmount()).isEqualTo(testCoupon.getAmount());

	}

}