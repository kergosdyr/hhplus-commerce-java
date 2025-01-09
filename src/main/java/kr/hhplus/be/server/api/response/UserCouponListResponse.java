package kr.hhplus.be.server.api.response;

import java.time.LocalDateTime;
import java.util.List;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;

public record UserCouponListResponse(long userId, List<UserCouponInfo> coupons) {

	public static UserCouponListResponse fromEntities(long userId, List<UserCoupon> userCoupons) {

		return new UserCouponListResponse(userId, userCoupons.stream().map(UserCouponInfo::fromEntity).toList());

	}


	public record UserCouponInfo(long userCouponId, long couponId, String couponName,
								 long amount, boolean isUsed,
								 LocalDateTime expiredAt) {

		public static UserCouponInfo fromEntity(UserCoupon userCoupon) {
			Coupon coupon = userCoupon.getCoupon();
			return new UserCouponInfo(userCoupon.getUserCouponId(), userCoupon.getCouponId(), coupon.getName(),
				coupon.getAmount(), userCoupon.isUsed(), userCoupon.getExpiredAt());

		}
	}

}
