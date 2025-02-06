package kr.hhplus.be.server.infra.storage.coupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {

	private final UserCouponJpaRepository userCouponJpaRepository;

	private final UserCouponRedissonRepository userCouponRedissonRepository;

	@Override
	public Optional<UserCoupon> findByUserIdAndCouponId(long userId, long couponId) {
		return userCouponJpaRepository.findByUserIdAndCouponId(userId, couponId);
	}

	@Override
	public UserCoupon save(UserCoupon userCoupon) {
		UserCoupon savedUserCoupon = userCouponJpaRepository.save(userCoupon);
		userCouponRedissonRepository.save(userCoupon);
		return savedUserCoupon;
	}

	@Override
	public List<UserCoupon> findAllByUserId(long userId) {
		return userCouponJpaRepository.findAllByUserId(userId);
	}

	@Override
	public boolean existsByUserIdAndCouponId(long userId, long couponId) {
		return userCouponRedissonRepository.existsByUserIdAndCouponId(userId, couponId);
	}

	@Override
	public List<UserCouponWaitOutput> findAllWait() {
		return userCouponRedissonRepository.findAllWait();
	}

	@Override
	public boolean publishUserCouponIssuedWait(long userId, long couponId, LocalDateTime issuedAt) {
		return userCouponRedissonRepository.publishCouponIssuedWait(userId, couponId, issuedAt);
	}
}
