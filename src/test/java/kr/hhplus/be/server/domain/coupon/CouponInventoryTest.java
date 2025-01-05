package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponInventoryTest {

	@Test
	@DisplayName("재고가 0이면 isIssuable()는 false를 반환한다.")
	void shouldReturnFalseWhenQuantityIsZero() {
		// given
		CouponInventory inventory = CouponInventory.builder()
			.inventoryId(1L)
			.couponId(1L)
			.quantity(0L)
			.build();

		// when
		boolean result = inventory.isIssuable();

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("재고가 1 이상이면 isIssuable()는 true를 반환한다.")
	void shouldReturnTrueWhenQuantityIsPositive() {
		// given
		CouponInventory inventory = CouponInventory.builder()
			.inventoryId(1L)
			.couponId(1L)
			.quantity(1L)
			.build();

		// when
		boolean result = inventory.isIssuable();

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("issue() 호출 시 재고가 1 감소한다.")
	void shouldDecreaseQuantityWhenIssue() {
		// given
		CouponInventory inventory = CouponInventory.builder()
			.inventoryId(1L)
			.couponId(1L)
			.quantity(1L)
			.build();

		// when
		inventory.issue();

		// then
		assertThat(inventory.getQuantity()).isEqualTo(0L);
	}
}
