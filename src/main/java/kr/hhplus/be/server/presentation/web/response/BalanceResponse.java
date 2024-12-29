package kr.hhplus.be.server.presentation.web.response;

public record BalanceResponse(Long userId, Long balance) {
	public static BalanceResponse mock(long userId) {
		return new BalanceResponse(userId, 150000L);
	}
}
