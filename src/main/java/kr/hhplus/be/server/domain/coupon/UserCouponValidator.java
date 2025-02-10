package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCouponValidator {

	private final UserCouponRepository userCouponRepository;

	public void validate(long userId, long couponId) {

		if (userCouponRepository.existsByUserIdAndCouponId(userId, couponId)) {
			throw new ApiException(ErrorType.COUPON_ALREADY_ISSUED);
		}
	}
}