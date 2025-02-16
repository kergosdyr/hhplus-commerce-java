package kr.hhplus.be.server.domain.analytics;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnalyticsServiceListener {

	private final AnalyticsSender analyticsSender;

	@KafkaListener(id = "myId", topics = "paymentSuccess")
	public void whenPaymentSuccess(PaymentSuccessEvent paymentSuccessEvent) {

		analyticsSender.send(
			new AnalyticData(paymentSuccessEvent.paymentId(), paymentSuccessEvent.orderId(),
				paymentSuccessEvent.orderCreatedAt())
		);
	}
}
