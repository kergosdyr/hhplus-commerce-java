package kr.hhplus.be.server.message;

import java.time.LocalDateTime;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.domain.analytics.AnalyticData;
import kr.hhplus.be.server.domain.analytics.AnalyticsService;
import kr.hhplus.be.server.message.payload.AnalyticsPaymentSuccessEventPayload;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnalyticsListener {

	private final AnalyticsService analyticsService;

	private final ObjectMapper objectMapper;

	@KafkaListener(id = "paymentSuccessListener", topics = "paymentSuccess")
	public void whenPaymentSuccess(
		ConsumerRecord<String, String> record
	) throws JsonProcessingException {

		String jsonValue = record.value();
		AnalyticsPaymentSuccessEventPayload analyticsPaymentSuccessEventPayload = objectMapper.readValue(jsonValue,
			AnalyticsPaymentSuccessEventPayload.class);

		analyticsService.analyze(
			new AnalyticData(analyticsPaymentSuccessEventPayload.paymentId(),
				analyticsPaymentSuccessEventPayload.orderId(),
				analyticsPaymentSuccessEventPayload.orderCreatedAt()),
			LocalDateTime.now()
		);
	}

}
