package kr.hhplus.be.server.domain.analytics;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.server.domain.payment.PaymentSuccess;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnalyticsServiceListener {

	private final AnalyticsSender analyticsSender;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void whenPaymentSuccess(PaymentSuccess paymentSuccess) {
		analyticsSender.send(
			new AnalyticData(paymentSuccess.paymentId(), paymentSuccess.orderId(), paymentSuccess.orderCreatedAt())
		);
	}
}
