package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;

@ExtendWith(MockitoExtension.class)
class CouponIssuerTest {

	@Mock
	CouponInventory mockInventory;
	@Mock
	UserCoupon savedUserCoupon;
	@Mock
	private CouponRepository couponRepository;
	@Mock
	private CouponInventoryRepository couponInventoryRepository;
	@Mock
	private UserCouponRepository userCouponRepository;
	@Mock
	private Coupon mockCoupon;
	@InjectMocks
	private CouponIssuer couponIssuer;

	@Test
	@DisplayName("쿠폰이 존재하지 않을 경우 ApiException(COUPON_NOT_FOUND)가 발생한다.")
	void shouldThrowApiExceptionWhenCouponNotFound() {
		// given
		long userId = 1L;
		long couponId = 999L;
		LocalDateTime issuedAt = LocalDateTime.now();

		when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> couponIssuer.issue(userId, couponId, issuedAt))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining(ErrorType.COUPON_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("쿠폰이 발급 가능한 상태(issuable)가 아닐 경우 ApiException(COUPON_NOT_ISSUABLE)가 발생한다.")
	void shouldThrowApiExceptionWhenCouponNotIssuable() {
		// given
		long userId = 1L;
		long couponId = 2L;
		LocalDateTime issuedAt = LocalDateTime.now();

		Coupon mockCoupon = mock(Coupon.class);
		when(mockCoupon.isIssuable(issuedAt)).thenReturn(false);

		when(couponRepository.findById(couponId)).thenReturn(Optional.of(mockCoupon));

		// when & then
		assertThatThrownBy(() -> couponIssuer.issue(userId, couponId, issuedAt))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining(ErrorType.COUPON_NOT_ISSUABLE.getMessage());
	}

	@Test
	@DisplayName("이미 발급된 쿠폰(동일 userId+couponId)이 존재한다면 ApiException(COUPON_ALREADY_ISSUED)를 발생시킨다.")
	void shouldThrowApiExceptionWhenCouponAlreadyIssued() {
		// given
		long userId = 1L;
		long couponId = 3L;
		LocalDateTime issuedAt = LocalDateTime.now();

		when(mockCoupon.isIssuable(issuedAt)).thenReturn(true);

		when(couponRepository.findById(couponId)).thenReturn(Optional.of(mockCoupon));
		when(userCouponRepository.findByUserIdAndCouponId(userId, couponId))
			.thenReturn(Optional.of(mock(UserCoupon.class)));

		// when & then
		assertThatThrownBy(() -> couponIssuer.issue(userId, couponId, issuedAt))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining(ErrorType.COUPON_ALREADY_ISSUED.getMessage());
	}

	@Test
	@DisplayName("쿠폰 인벤토리가 존재하지 않는 경우, ApiException(COUPON_NOT_FOUND)를 발생시킨다.")
	void shouldThrowApiExceptionWhenCouponInventoryNotFound() {
		// given
		long userId = 1L;
		long couponId = 4L;
		LocalDateTime issuedAt = LocalDateTime.now();

		when(mockCoupon.isIssuable(issuedAt)).thenReturn(true);

		when(couponRepository.findById(couponId)).thenReturn(Optional.of(mockCoupon));
		when(userCouponRepository.findByUserIdAndCouponId(userId, couponId))
			.thenReturn(Optional.empty());
		when(couponInventoryRepository.findByCouponId(couponId))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> couponIssuer.issue(userId, couponId, issuedAt))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining(ErrorType.COUPON_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("정상적으로 쿠폰 발급이 가능하면, 인벤토리를 차감 후 UserCoupon을 저장하고 반환한다.")
	void shouldIssueCouponSuccessfully() {
		// given
		long userId = 1L;
		long couponId = 5L;
		LocalDateTime issuedAt = LocalDateTime.of(2024, 12, 31, 0, 0, 0);

		// Coupon mock
		when(mockCoupon.isIssuable(issuedAt)).thenReturn(true);
		when(mockCoupon.getCouponId()).thenReturn(couponId);
		when(mockCoupon.getAmount()).thenReturn(1000L);
		when(mockCoupon.getExpiredAt()).thenReturn(LocalDateTime.now().plusDays(7));
		when(mockInventory.isIssuable()).thenReturn(true);

		when(couponRepository.findById(couponId)).thenReturn(Optional.of(mockCoupon));

		when(userCouponRepository.findByUserIdAndCouponId(userId, couponId))
			.thenReturn(Optional.empty());

		when(couponInventoryRepository.findByCouponId(couponId))
			.thenReturn(Optional.of(mockInventory));

		when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(savedUserCoupon);

		// when
		UserCoupon result = couponIssuer.issue(userId, couponId, issuedAt);

		// then
		verify(mockInventory, times(1)).issue();
		verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
		assertThat(result).isEqualTo(savedUserCoupon);
	}

}
