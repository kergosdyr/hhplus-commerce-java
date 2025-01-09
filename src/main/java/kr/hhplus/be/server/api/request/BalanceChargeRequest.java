package kr.hhplus.be.server.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BalanceChargeRequest(
	@NotNull(message = "userId는 필수값입니다.")
	@Min(value = 1, message = "userId는 1 이상의 값이어야 합니다.")
	Long userId,

	@NotNull(message = "amount는 필수값입니다.")
	@Min(value = 100, message = "최소 100원 이상 충전 가능합니다.")
	Long amount
) {
}
