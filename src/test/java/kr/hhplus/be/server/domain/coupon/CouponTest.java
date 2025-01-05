package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponTest {

	private static Coupon createCoupon(LocalDateTime now, CouponInventory couponInventory, boolean isExpired) {
		return Coupon.builder()
			.couponId(1L)
			.name("Test Coupon")
			.amount(1000L)
			.status("ACTIVE")
			.expiredAt(isExpired ? now.minusDays(1) : now.plusDays(1))
			.couponInventory(couponInventory)
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
		LocalDateTime now = LocalDateTime.now();

		CouponInventory couponInventory = createCouponInventory(1L, 10L);

		Coupon coupon = createCoupon(now, couponInventory, true);

		// when
		boolean result = coupon.isIssuable(now);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("쿠폰 인벤토리 재고가 0이라면, isIssuable()는 false를 반환한다.")
	void shouldReturnFalseWhenInventoryIsEmpty() {
		// given
		LocalDateTime now = LocalDateTime.now();

		CouponInventory couponInventory = createCouponInventory(1L, 0L);

		Coupon coupon = createCoupon(now, couponInventory, false);

		// when
		boolean result = coupon.isIssuable(now);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("만료 전이고 재고가 1 이상이라면, isIssuable()는 true를 반환한다.")
	void shouldReturnTrueWhenCouponIsValidAndInventoryAvailable() {
		// given
		LocalDateTime now = LocalDateTime.now();

		CouponInventory couponInventory = createCouponInventory(2L, 5L);

		Coupon coupon = createCoupon(now, couponInventory, false);

		// when
		boolean result = coupon.isIssuable(now);

		// then
		assertThat(result).isTrue();
	}

}
