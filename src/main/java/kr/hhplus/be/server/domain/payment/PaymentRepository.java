package kr.hhplus.be.server.domain.payment;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public interface PaymentRepository {

	Payment save(Payment payment);

	PaymentSuccessEventRecord savePaymentEvent(PaymentSuccessEventRecord paymentSuccessEventRecord);

	List<PaymentSuccessEventRecord> findAllNotSentEvent();


}
