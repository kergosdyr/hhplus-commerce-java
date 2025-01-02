package kr.hhplus.be.server.api.response;

import java.util.List;

public record UserCouponListResponse(long userId, List<UserCouponInfo> coupons) {

	public static UserCouponListResponse mock(long userId) {
		return new UserCouponListResponse(1L, List.of(
			new UserCouponInfo(111L, 999L, "10% Discount", 10, true, false, "2024-01-31T23:59:59"),
			new UserCouponInfo(112L, 1000L, "3000원 할인 쿠폰", 3000, false, true, "2024-02-15T23:59:59")));
	}

	public record UserCouponInfo(long userCouponId, long couponId, String couponName,
								 int discountValue, boolean isPercent, boolean used,
								 String expiredAt) {
	}

}
