package kr.hhplus.be.server.domain.payment;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentSuccessEventScheduler {

	private final PaymentRepository paymentRepository;

	private final PaymentEventPublisher paymentEventPublisher;

	@Scheduled(cron = "0 */5 * * * *")
	@Transactional
	public void paymentSuccessEventReSendSchedule() {

		paymentRepository.findAllFailedRecord().stream()
			.map(PaymentSuccessEvent::fromRecord)
			.forEach(paymentEventPublisher::success);

	}

}
