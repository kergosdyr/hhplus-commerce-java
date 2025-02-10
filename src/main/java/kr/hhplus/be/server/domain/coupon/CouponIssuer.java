package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponIssuer {

	private final UserCouponValidator userCouponValidator;

	private final CouponReader couponReader;

	private final CouponPublisher couponPublisher;

	public UserCoupon issue(long userId, long couponId, LocalDateTime issuedAt) {

		userCouponValidator.validate(userId, couponId);

		var coupon = couponReader.readIssuable(couponId, issuedAt);

		boolean isWaitSuccess = couponPublisher.publishCouponIssueWait(userId, couponId, issuedAt);

		if (!isWaitSuccess) {
			throw new ApiException(ErrorType.COUPON_NOT_ISSUABLE);
		}

		return UserCoupon.fromCoupon(coupon, userId, issuedAt);

	}

}
