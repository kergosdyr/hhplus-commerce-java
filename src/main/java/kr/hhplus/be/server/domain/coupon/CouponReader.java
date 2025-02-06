package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponReader {

	private final CouponRepository couponRepository;

	public Coupon readIssuable(long couponId, LocalDateTime issuedAt) {
		var coupon = couponRepository.findById(couponId)
			.orElseThrow(() -> new ApiException(ErrorType.COUPON_NOT_FOUND));

		if (!coupon.isIssuable(issuedAt)) {
			throw new ApiException(ErrorType.COUPON_NOT_ISSUABLE);
		}
		return coupon;
	}
}
