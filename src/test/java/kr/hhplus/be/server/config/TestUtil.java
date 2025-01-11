package kr.hhplus.be.server.config;

import java.time.LocalDateTime;

import kr.hhplus.be.server.domain.balanace.Balance;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponInventory;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.enums.UserCouponStatus;

public abstract class TestUtil {

	public static Balance createTestBalance() {
		return Balance.builder()
			.balanceId(1L)
			.userId(100L)
			.amount(1000L)
			.build();
	}

	public static User createTestUser() {
		return User.builder()
			.name("전진")
			.build();
	}

	public static Coupon createTestCoupon(LocalDateTime expiredAt) {
		return Coupon.builder()
			.amount(1000L)
			.expiredAt(expiredAt)
			.name("전진 쿠폰")
			.status("VALID")
			.build();
	}

	public static CouponInventory createTestCouponInventory(long couponId, long quantity) {
		return CouponInventory.builder()
			.couponId(couponId)
			.quantity(quantity)
			.build();
	}

	public static UserCoupon createTestUserCoupon(long userId, long couponId) {
		return UserCoupon.builder()
			.couponId(couponId)
			.userId(userId)
			.amount(1000L)
			.issuedAt(LocalDateTime.of(2024, 12, 31, 0, 0))
			.expiredAt(LocalDateTime.of(2025, 1, 2, 0, 0))
			.status(UserCouponStatus.AVAILABLE)
			.build();
	}

	public static Balance getTestBalance(long userId, long amount) {
		return Balance.builder()
			.amount(amount)
			.userId(userId)
			.build();
	}
}
