package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponApplier {

	public final UserCouponFinder userCouponFinder;

	public long apply(long totalPrice, long userId, long couponId) {

		UserCoupon userCoupon = userCouponFinder.find(userId, couponId);
		return userCoupon.use(totalPrice);
	}
}
