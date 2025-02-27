package kr.hhplus.be.server.infra.kafka.payment;

import java.time.LocalDateTime;

public record PaymentSuccessEventPayload(
	long orderId,
	long paymentId,
	LocalDateTime orderCreatedAt

) {
}
