package kr.hhplus.be.server.infra.kafka.payment;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.PaymentEventClient;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventClientImpl implements PaymentEventClient {

	private final KafkaTemplate<Long, PaymentSuccessEventPayload> kafkaTemplate;

	@Override
	public void send(PaymentSuccessEventPayload payload) {
		kafkaTemplate.send("paymentSuccess", payload);
	}
}
