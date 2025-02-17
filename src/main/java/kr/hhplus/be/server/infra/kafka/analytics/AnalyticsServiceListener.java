package kr.hhplus.be.server.infra.kafka.analytics;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.analytics.AnalyticData;
import kr.hhplus.be.server.infra.clients.analytics.AnalyticsClientImpl;
import kr.hhplus.be.server.infra.kafka.payment.PaymentSuccessEventPayload;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnalyticsServiceListener {

	private final AnalyticsClientImpl analyticsSender;

	@KafkaListener(id = "paymentSuccessListener", topics = "paymentSuccess")
	public void whenPaymentSuccess(PaymentSuccessEventPayload paymentSuccessEventPayload) {

		analyticsSender.send(
			new AnalyticData(paymentSuccessEventPayload.paymentId(), paymentSuccessEventPayload.orderId(),
				paymentSuccessEventPayload.orderCreatedAt())
		);
	}
}
