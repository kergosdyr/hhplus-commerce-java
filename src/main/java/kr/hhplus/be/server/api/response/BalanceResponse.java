package kr.hhplus.be.server.api.response;

public record BalanceResponse(Long userId, Long balance) {
	public static BalanceResponse mock(long userId) {
		return new BalanceResponse(userId, 150000L);
	}
}
