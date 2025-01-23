package kr.hhplus.be.server.domain.order;

public record OrderCommand(
	long quantity, long productId
) {
}
