package kr.hhplus.be.server.infra.storage.payment;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEventRecord;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

	private final PaymentJpaRepository paymentJpaRepository;

	private final PaymentSuccessEventRecordJpaRepository paymentSuccessEventRecordJpaRepository;

	@Override
	public Payment save(Payment payment) {
		return paymentJpaRepository.save(payment);
	}

	@Override
	public PaymentSuccessEventRecord savePaymentEvent(PaymentSuccessEventRecord paymentSuccessEventRecord) {
		return paymentSuccessEventRecordJpaRepository.save(paymentSuccessEventRecord);
	}

	@Override
	public Optional<PaymentSuccessEventRecord> findByPaymentId(long paymentId) {
		return paymentSuccessEventRecordJpaRepository.findByPaymentId(paymentId);
	}

	@Override
	public List<PaymentSuccessEventRecord> findAllFailedRecord() {
		return paymentSuccessEventRecordJpaRepository.findAllFailedRecord();
	}
}
