package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponPublisher {

	private final UserCouponRepository userCouponRepository;

	public boolean publishCouponIssueWait(long userId, long couponId, LocalDateTime issuedAt) {
		return userCouponRepository.publishUserCouponIssuedWait(userId, couponId, issuedAt);
	}
}
