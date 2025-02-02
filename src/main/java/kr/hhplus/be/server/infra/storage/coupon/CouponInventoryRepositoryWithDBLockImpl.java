package kr.hhplus.be.server.infra.storage.coupon;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.coupon.CouponInventory;
import kr.hhplus.be.server.domain.coupon.CouponInventoryRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponInventoryRepositoryWithDBLockImpl implements CouponInventoryRepository {

	private final CouponInventoryJpaRepository couponInventoryJpaRepository;

	@Override
	public Optional<CouponInventory> findByCouponId(long couponId) {
		return couponInventoryJpaRepository.findByCouponIdWithLock(couponId);
	}
}
