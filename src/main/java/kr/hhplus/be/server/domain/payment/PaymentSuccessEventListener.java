package kr.hhplus.be.server.domain.payment;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentSuccessEventListener {

	private final PaymentEventClient paymentEventClient;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void sendMessageWhenPaymentSuccess(PaymentSuccessEvent paymentSuccessEvent) {
		paymentEventClient.send(paymentSuccessEvent.toPayload());
	}

}
