package kr.hhplus.be.server.domain.payment;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public interface PaymentRepository {

	Payment save(Payment payment);

	PaymentSuccessEventRecord savePaymentEvent(PaymentSuccessEventRecord paymentSuccessEventRecord);

	Optional<PaymentSuccessEventRecord> findByPaymentId(long paymentId);

	List<PaymentSuccessEventRecord> findAllFailedRecord();


}
