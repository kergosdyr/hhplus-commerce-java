package kr.hhplus.be.server.api.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CouponIssueRequest(
	@NotNull(message = "userId는 필수값입니다.")
	@Min(value = 1, message = "userId는 1 이상의 값이어야 합니다.")
	long userId,

	@NotNull(message = "couponId는 필수값입니다.")
	@Min(value = 1, message = "couponId는 1 이상의 값이어야 합니다.")
	long couponId,

	@NotNull(message = "issuedAt은 필수값입니다.")
	LocalDateTime issuedAt
) {
}
