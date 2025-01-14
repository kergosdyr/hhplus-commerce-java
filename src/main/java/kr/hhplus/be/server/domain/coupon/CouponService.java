package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.user.UserFinder;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

	private final CouponIssuer couponIssuer;

	private final UserCouponFinder couponLoader;

	private final UserFinder userFinder;

	public UserCoupon issue(long userId, long couponId, LocalDateTime issuedAt) {

		if (userFinder.notExistsByUserId(userId)) {
			throw new ApiException(ErrorType.USER_NOT_FOUND);
		}

		return couponIssuer.issue(userId, couponId, issuedAt);
	}

	public List<UserCoupon> load(long userId) {

		if (userFinder.notExistsByUserId(userId)) {
			throw new ApiException(ErrorType.USER_NOT_FOUND);
		}

		return couponLoader.findAllByUserId(userId);

	}

}
