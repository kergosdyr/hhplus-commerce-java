package kr.hhplus.be.server.api.response;

import java.time.LocalDateTime;

import kr.hhplus.be.server.domain.coupon.UserCoupon;

public record UserCouponResponse(long userCouponId, long userId, long couponId, LocalDateTime issuedAt, String status) {

	public static UserCouponResponse fromEntity(UserCoupon userCoupon) {
		return new UserCouponResponse(userCoupon.getUserCouponId(), userCoupon.getUserId(), userCoupon.getCouponId(),
			userCoupon.getIssuedAt(), userCoupon.getStatus().name());
	}

}
