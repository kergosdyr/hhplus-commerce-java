package kr.hhplus.be.server.domain.coupon;

import static kr.hhplus.be.server.error.ErrorType.COUPON_NOT_FOUND;
import static kr.hhplus.be.server.error.ErrorType.COUPON_NOT_ISSUABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.error.ApiException;

@ExtendWith(MockitoExtension.class)
class CouponReaderTest {

	@Mock
	private CouponRepository couponRepository;

	@InjectMocks
	private CouponReader couponReader;

	@Test
	@DisplayName("readIssuable 함수를 호출했을 때 해당하는 Coupon 이 존재하지 않는 경우, ApiException(ErroType.COUPON_NOT_FOUND)를 던진다")
	void shouldThrowApiExceptionWhenCouponNotFound() {
		//given
		long couponId = 1L;
		when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

		//when & then
		assertThatThrownBy(() -> couponReader.readIssuable(couponId, LocalDateTime.of(2025, 1, 1, 0, 0))).isInstanceOf(
			ApiException.class).hasMessageContaining(COUPON_NOT_FOUND.getMessage());

	}

	@Test
	@DisplayName("readIssuable 을 호출했을 때 해당하는 쿠폰이 만료된 경우 ApiException(ErrorType.COUPON_NOT_ISSUABLE) 을 던진다")
	void shouldThrowApiExceptionWhenCouponExpired() {
		//given
		long couponId = 1L;

		when(couponRepository.findById(couponId)).thenReturn(
			Optional.of(TestUtil.createTestCoupon(LocalDateTime.of(2024, 12, 31, 0, 0))));

		//when
		assertThatThrownBy(() -> {
			couponReader.readIssuable(couponId, LocalDateTime.of(2025, 1, 1, 0, 0));
		}).isInstanceOf(ApiException.class).hasMessageContaining(COUPON_NOT_ISSUABLE.getMessage());

	}

	@Test
	@DisplayName("readIssuable 을 호출했을 때 해당하는 쿠폰이 만료되지않고, 존재하는 경우 Coupon 을 반환한다")
	void shouldReturnCouponWhenCouponIsNormal() {
		//given
		long couponId = 1L;

		Coupon mockCoupon = TestUtil.createTestCoupon(LocalDateTime.of(2025, 12, 31, 0, 0));
		when(couponRepository.findById(couponId)).thenReturn(Optional.of(mockCoupon));

		//when
		Coupon coupon = couponReader.readIssuable(couponId, LocalDateTime.of(2025, 1, 1, 0, 0));

		//then
		assertThat(coupon).isEqualTo(mockCoupon);

	}

}