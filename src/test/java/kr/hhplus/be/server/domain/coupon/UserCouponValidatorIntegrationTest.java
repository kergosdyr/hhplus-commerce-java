package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;

class UserCouponValidatorIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("UserCoupon 발급 이력이 이미 존재하는 경우 validate 를 호출하면 ApiException(COUPON_ALREADY_ISSUED)를 던진다")
	void shouldThrowApiExceptionWhenUserCouponAlreadyExist() {
		//given
		long userId = 123L;
		long couponId = 456L;
		UserCoupon userCoupon = TestUtil.createTestUserCoupon(userId, couponId);
		userCouponRedissonRepository.save(userCoupon);

		//when & then

		assertThatThrownBy(() -> {
			userCouponValidator.validate(userId, couponId);
		}).isInstanceOf(ApiException.class)
			.hasMessageContaining(ErrorType.COUPON_ALREADY_ISSUED.getMessage());

	}

	@Test
	@DisplayName("UserCoupon 발급/요청 이력이 이미 존재하지 않는 경우 경우 validate 를 호출하면 정상 호출된다.")
	void shouldDoNothingWhenUserCouponNotExist() {
		//given
		long userId = 123L;
		long couponId = 456L;
		UserCoupon userCoupon = TestUtil.createTestUserCoupon(userId, couponId);

		//when & then

		Assertions.assertThatNoException().isThrownBy(() -> userCouponValidator.validate(userId, couponId));

	}

}