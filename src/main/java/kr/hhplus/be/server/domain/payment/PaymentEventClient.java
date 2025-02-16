package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.infra.kafka.payment.PaymentSuccessEventPayload;

@Component
public interface PaymentEventClient {
	void send(PaymentSuccessEventPayload payload);
}
