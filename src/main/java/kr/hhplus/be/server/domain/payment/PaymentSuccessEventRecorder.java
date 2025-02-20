package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentSuccessEventRecorder {

	private final PaymentRepository paymentRepository;

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void recordEventWhenPaymentSuccess(PaymentSuccessEvent paymentSuccessEvent) {
		paymentRepository.savePaymentEvent(paymentSuccessEvent.toRecord());
	}

}
