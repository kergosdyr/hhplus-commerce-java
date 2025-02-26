package kr.hhplus.be.server.infra.storage.coupon;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

	private final CouponJpaRepository couponJpaRepository;

	private final CouponRedissonRepository couponRedissonRepository;



	@Override
	public Optional<Coupon> findById(long couponId) {

		return couponJpaRepository.findById(couponId);
	}

	@Override
	public void issue(long couponId) {

		couponRedissonRepository.issue(couponId);

	}
}
