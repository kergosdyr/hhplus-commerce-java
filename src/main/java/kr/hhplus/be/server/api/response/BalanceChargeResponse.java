package kr.hhplus.be.server.api.response;

import kr.hhplus.be.server.domain.balanace.Balance;

public record BalanceChargeResponse(Long userId, Long balance) {

	public static BalanceChargeResponse fromEntity(Balance balance) {
		return new BalanceChargeResponse(balance.getUserId(), balance.getAmount());
	}

}
