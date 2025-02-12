package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;

public record PaymentSuccess(
	long orderId,
	long paymentId,
	LocalDateTime orderCreatedAt
) {
}
