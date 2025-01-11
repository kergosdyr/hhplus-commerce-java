package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.payment.Payment;

public record OrderPayment(
	Order order, Payment payment
) {
}
