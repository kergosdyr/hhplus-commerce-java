package kr.hhplus.be.server.api.response;

public record BalanceChargeResponse(Long userId, Long balance) {

	public static BalanceChargeResponse mock(long userId) {
		return new BalanceChargeResponse(userId, 150000L);
	}

}
