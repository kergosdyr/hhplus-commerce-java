package kr.hhplus.be.server.message.payload;

import java.time.LocalDateTime;

public record AnalyticsPaymentSuccessEventPayload(
	long orderId,
	long paymentId,
	LocalDateTime orderCreatedAt

) {
}
