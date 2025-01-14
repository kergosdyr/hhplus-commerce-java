package kr.hhplus.be.server.domain.coupon;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCouponFinder {

	private final UserCouponRepository userCouponRepository;

	public List<UserCoupon> findAllByUserId(long userId) {
		return userCouponRepository.findAllByUserId(userId);
	}

	public UserCoupon find(long userId, long couponId) {

		return userCouponRepository.findByUserIdAndCouponId(userId, couponId)
			.orElseThrow(() -> new ApiException(ErrorType.BALANCE_NOT_FOUND));
	}
}
