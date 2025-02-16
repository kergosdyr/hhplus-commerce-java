package kr.hhplus.be.server.infra.storage.payment;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEventRecord;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

	private final PaymentJpaRepository paymentJpaRepository;

	private final PaymentEventRecordJpaRepository paymentEventRecordJpaRepository;

	@Override
	public Payment save(Payment payment) {
		return paymentJpaRepository.save(payment);
	}

	@Override
	public PaymentSuccessEventRecord savePaymentEvent(PaymentSuccessEventRecord paymentSuccessEventRecord) {
		return paymentEventRecordJpaRepository.save(paymentSuccessEventRecord);
	}

	@Override
	public List<PaymentSuccessEventRecord> findAllNotSentEvent() {
		return paymentEventRecordJpaRepository.findAllByStatusReady();
	}
}
