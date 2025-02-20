package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentSuccessEventRecodeReader {

	private final PaymentRepository paymentRepository;

	public PaymentSuccessEventRecord read(long paymentId) {

		return paymentRepository.findByPaymentId(paymentId).orElseThrow(
			() -> new ApiException(ErrorType.PAYMENT_RECORDER_NOT_FOUND)
		);

	}

}
