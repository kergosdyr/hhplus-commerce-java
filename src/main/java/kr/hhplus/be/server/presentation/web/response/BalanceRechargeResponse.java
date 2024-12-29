package kr.hhplus.be.server.presentation.web.response;

public record BalanceRechargeResponse(Long userId, Long balance) {

	public static BalanceRechargeResponse mock(long userId) {
		return new BalanceRechargeResponse(userId, 150000L);
	}

}
