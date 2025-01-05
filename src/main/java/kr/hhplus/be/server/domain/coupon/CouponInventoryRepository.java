package kr.hhplus.be.server.domain.coupon;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public interface CouponInventoryRepository {
	Optional<CouponInventory> findByCouponId(long couponId);
}
