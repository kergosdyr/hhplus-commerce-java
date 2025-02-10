package kr.hhplus.be.server.domain.coupon;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public interface CouponRepository {
	Optional<Coupon> findById(long couponId);

	void issue(long couponId);
}
