package kr.hhplus.be.server.domain.analytics;

import java.time.LocalDateTime;

public record AnalyticData(
	long paymentId,
	long orderId,
	LocalDateTime orderCreatedAt
) {
}
