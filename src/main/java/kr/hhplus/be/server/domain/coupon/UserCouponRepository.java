package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.infra.storage.coupon.UserCouponWaitOutput;

@Component
public interface UserCouponRepository {
	Optional<UserCoupon> findByUserIdAndCouponId(long userId, long couponId);

	UserCoupon save(UserCoupon userCoupon);

	List<UserCoupon> findAllByUserId(long userId);

	boolean existsByUserIdAndCouponId(long userId, long couponId);

	List<UserCouponWaitOutput> findAllWait();

	boolean publishUserCouponIssuedWait(long userId, long couponId, LocalDateTime issuedAt);
}
