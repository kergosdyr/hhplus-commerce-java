package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.payment.Payment;

public record OrderOutput(
	Order order, Payment payment
) {
}
