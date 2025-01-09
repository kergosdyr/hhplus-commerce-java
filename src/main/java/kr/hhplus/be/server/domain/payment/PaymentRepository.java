package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Component;

@Component
public interface PaymentRepository {

	Payment save(Payment payment);
}
