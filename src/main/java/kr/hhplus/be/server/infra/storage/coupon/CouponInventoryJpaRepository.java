package kr.hhplus.be.server.infra.storage.coupon;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.hhplus.be.server.domain.coupon.CouponInventory;

public interface CouponInventoryJpaRepository extends JpaRepository<CouponInventory, Long> {

	@Query("select c from CouponInventory c where c.couponId = ?1")
	Optional<CouponInventory> findByCouponIdWithLock(long couponId);
}
