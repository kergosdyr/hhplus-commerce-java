package kr.hhplus.be.server.api.response;

import kr.hhplus.be.server.domain.balanace.Balance;

public record BalanceResponse(Long userId, Long balance) {

	public static BalanceResponse fromEntity(Balance balance) {
		return new BalanceResponse(balance.getUserId(), balance.getAmount());
	}

}
