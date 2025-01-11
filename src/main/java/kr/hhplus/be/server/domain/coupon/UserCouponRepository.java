package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public interface UserCouponRepository {
	Optional<UserCoupon> findByUserIdAndCouponId(long userId, long couponId);

	UserCoupon save(UserCoupon userCoupon);

	List<UserCoupon> findAllByUserId(long userId);

}
