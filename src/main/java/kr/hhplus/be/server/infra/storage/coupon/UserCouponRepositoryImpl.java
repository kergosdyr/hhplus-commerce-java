package kr.hhplus.be.server.infra.storage.coupon;

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

	@Override
	public Optional<UserCoupon> findByUserIdAndCouponId(long userId, long couponId) {
		return userCouponJpaRepository.findByUserIdAndCouponId(userId, couponId);
	}

	@Override
	public UserCoupon save(UserCoupon userCoupon) {
		return userCouponJpaRepository.save(userCoupon);
	}

	@Override
	public List<UserCoupon> findAllByUserId(long userId) {
		return userCouponJpaRepository.findAllByUserId(userId);
	}
}
