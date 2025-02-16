package kr.hhplus.be.server.domain.payment;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentEventPublisher {

	private final ApplicationEventPublisher applicationEventPublisher;

	public void success(PaymentSuccessEvent paymentSuccessEvent) {
		applicationEventPublisher.publishEvent(paymentSuccessEvent);
	}
}
