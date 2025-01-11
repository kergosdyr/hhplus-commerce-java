package kr.hhplus.be.server.infra.storage.coupon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.coupon.UserCoupon;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {
	Optional<UserCoupon> findByUserIdAndCouponId(long userId, long couponId);

	List<UserCoupon> findAllByUserId(long userId);

}
