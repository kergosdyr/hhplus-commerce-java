package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import kr.hhplus.be.server.enums.UserCouponStatus;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponIssuer {

	private final CouponRepository couponRepository;

	private final CouponInventoryRepository couponInventoryRepository;

	private final UserCouponRepository userCouponRepository;

	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 6, // 재시도 횟수
		backoff = @Backoff(delay = 300, multiplier = 1.2) // 재시도 간격(밀리초 단위)
	)
	@Transactional
	public UserCoupon issue(long userId, long couponId, LocalDateTime issuedAt) {

		var coupon = couponRepository.findById(couponId)
			.orElseThrow(() -> new ApiException(ErrorType.COUPON_NOT_FOUND));

		if (!coupon.isIssuable(issuedAt)) {
			throw new ApiException(ErrorType.COUPON_NOT_ISSUABLE);
		}

		userCouponRepository.findByUserIdAndCouponId(userId, couponId)
			.ifPresent(userCoupon -> {
				throw new ApiException(ErrorType.COUPON_ALREADY_ISSUED);
			});

		CouponInventory couponInventory = couponInventoryRepository.findByCouponId(couponId)
			.orElseThrow(() -> new ApiException(ErrorType.COUPON_NOT_FOUND));

		if (!couponInventory.isIssuable()) {
			throw new ApiException(ErrorType.COUPON_NOT_ISSUABLE);
		}

		couponInventory.issue();

		var userCoupon = UserCoupon.builder()
			.couponId(coupon.getCouponId())
			.userId(userId)
			.amount(coupon.getAmount())
			.expiredAt(coupon.getExpiredAt())
			.status(UserCouponStatus.AVAILABLE)
			.issuedAt(issuedAt)
			.build();

		return userCouponRepository.save(userCoupon);

	}
}
