package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.support.WithLock;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponApplier {

	public final UserCouponFinder userCouponFinder;

	@Transactional
	@WithLock(key = "'user_coupon:'.concat(#couponId)")
	public long apply(long totalPrice, long userId, long couponId) {

		UserCoupon userCoupon = userCouponFinder.find(userId, couponId);
		return userCoupon.use(totalPrice);
	}
}
