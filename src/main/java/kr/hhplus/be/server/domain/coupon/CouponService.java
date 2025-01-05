package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.user.UserValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

	private final CouponIssuer couponIssuer;

	private final UserCouponLoader couponLoader;

	private final UserValidator userValidator;

	public UserCoupon issue(long userId, long couponId, LocalDateTime issuedAt) {

		userValidator.validate(userId);

		return couponIssuer.issue(userId, couponId, issuedAt);
	}

	public List<UserCoupon> load(long userId) {

		userValidator.validate(userId);

		return couponLoader.loadAllByUserId(userId);

	}




}
