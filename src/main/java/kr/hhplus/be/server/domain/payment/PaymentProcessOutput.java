package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.Order;

public record PaymentProcessOutput(
	Payment payment, Order order
) {
}
