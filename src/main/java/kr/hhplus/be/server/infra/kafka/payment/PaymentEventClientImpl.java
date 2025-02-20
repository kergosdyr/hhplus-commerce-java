package kr.hhplus.be.server.infra.kafka.payment;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.domain.payment.PaymentEventClient;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventClientImpl implements PaymentEventClient {

	private final KafkaTemplate<Long, String> kafkaTemplate;

	private final ObjectMapper objectMapper;

	@Override
	public void send(PaymentSuccessEventPayload payload) {
		try {
			kafkaTemplate.send("paymentSuccess", objectMapper.writeValueAsString(payload));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
