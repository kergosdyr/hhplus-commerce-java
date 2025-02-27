package kr.hhplus.be.server.domain.payment;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentSuccessEventListener {

	private final PaymentEventClient paymentEventClient;

	private final PaymentSuccessEventRecodeModifier paymentSuccessEventRecodeModifier;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional
	public void sendMessageWhenPaymentSuccess(PaymentSuccessEvent paymentSuccessEvent) {
		try {
			paymentEventClient.send(paymentSuccessEvent.toPayload());
			paymentSuccessEventRecodeModifier.sending(paymentSuccessEvent.paymentId());
		} catch (Exception e) {
			paymentSuccessEventRecodeModifier.failed(paymentSuccessEvent.paymentId());
		}
	}

}
