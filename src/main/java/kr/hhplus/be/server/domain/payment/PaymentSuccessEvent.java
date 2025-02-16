package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;

import kr.hhplus.be.server.infra.kafka.payment.PaymentSuccessEventPayload;

public record PaymentSuccessEvent(
	long orderId,
	long paymentId,
	LocalDateTime orderCreatedAt
) {
	public static PaymentSuccessEvent fromRecord(PaymentSuccessEventRecord record) {
		return new PaymentSuccessEvent(
			record.getOrderId(),
			record.getPaymentId(),
			record.getOrderCreatedAt()
		);
	}

	public PaymentSuccessEventPayload toPayload() {
		return new PaymentSuccessEventPayload(orderId, paymentId, orderCreatedAt);
	}

	public PaymentSuccessEventRecord toRecord() {
		return PaymentSuccessEventRecord.builder()
			.orderId(orderId)
			.paymentId(paymentId)
			.orderCreatedAt(orderCreatedAt)
			.build();
	}
}
