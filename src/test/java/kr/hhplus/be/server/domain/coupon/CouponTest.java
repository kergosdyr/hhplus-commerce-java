package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.enums.CouponStatus;

@ExtendWith(MockitoExtension.class)
class CouponTest {

	private static Coupon createCoupon(CouponInventory couponInventory, LocalDateTime expiredAt) {
		return Coupon.builder()
			.couponId(1L)
			.name("Test Coupon")
			.amount(1000L)
			.status(CouponStatus.ACTIVE)
			.expiredAt(expiredAt)
			.build();
	}

	private static CouponInventory createCouponInventory(long inventoryId, long quantity) {
		return CouponInventory.builder()
			.inventoryId(inventoryId)
			.couponId(inventoryId)
			.quantity(quantity)   // 충분한 재고
			.build();
	}

	@Test
	@DisplayName("만료 시간이 현재보다 이전이라면, isIssuable()는 false를 반환한다.")
	void shouldReturnFalseWhenExpired() {
		// given
		LocalDateTime issuedAt = LocalDateTime.of(2025, 1, 2, 0, 0, 0, 0);

		CouponInventory couponInventory = createCouponInventory(1L, 10L);

		Coupon coupon = createCoupon(couponInventory, LocalDateTime.of(2024, 12, 30, 0, 0, 0, 0));

		// when
		boolean result = coupon.isIssuable(issuedAt);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("만료 시간이 현재보다 이후이라면, isIssuable()는 true를 반환한다.")
	void shouldReturnTrueWhenNotExpired() {
		// given
		LocalDateTime issuedAt = LocalDateTime.of(2024, 12, 30, 0, 0, 0, 0);

		CouponInventory couponInventory = createCouponInventory(1L, 10L);

		Coupon coupon = createCoupon(couponInventory, LocalDateTime.of(2024, 12, 31, 0, 0, 0, 0));

		// when
		boolean result = coupon.isIssuable(issuedAt);

		// then
		assertThat(result).isTrue();
	}

}
