package kr.hhplus.be.server.infra.storage.coupon;

import java.time.LocalDateTime;

public record UserCouponWaitOutput(
	long userId,
	long couponId,
	LocalDateTime issuedAt
) {

}
