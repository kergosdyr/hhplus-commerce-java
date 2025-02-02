package kr.hhplus.be.server.infra.storage.coupon;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.coupon.CouponInventory;
import kr.hhplus.be.server.domain.coupon.CouponInventoryRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile({"lettuce", "redisson"})
@Primary
public class CouponInventoryRepositoryImpl implements CouponInventoryRepository {

	private final CouponInventoryJpaRepository couponInventoryJpaRepository;

	@Override
	public Optional<CouponInventory> findByCouponId(long couponId) {
		return couponInventoryJpaRepository.findByCouponId(couponId);
	}
}
