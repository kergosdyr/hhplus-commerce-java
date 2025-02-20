package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PaymentSuccessEventRecodeModifier {

	private final PaymentSuccessEventRecodeReader paymentSuccessEventRecodeReader;

	@Transactional
	public PaymentSuccessEventRecord success(long paymentId, LocalDateTime sentAt) {
		PaymentSuccessEventRecord paymentSuccessEventRecord = paymentSuccessEventRecodeReader.read(paymentId);
		paymentSuccessEventRecord.success(sentAt);
		return paymentSuccessEventRecord;
	}

	@Transactional
	public PaymentSuccessEventRecord failed(long paymentId) {
		PaymentSuccessEventRecord paymentSuccessEventRecord = paymentSuccessEventRecodeReader.read(paymentId);
		paymentSuccessEventRecord.failed();
		return paymentSuccessEventRecord;
	}

	@Transactional
	public PaymentSuccessEventRecord sending(long paymentId) {
		PaymentSuccessEventRecord paymentSuccessEventRecord = paymentSuccessEventRecodeReader.read(paymentId);
		paymentSuccessEventRecord.sending();
		return paymentSuccessEventRecord;
	}

}
