package kr.hhplus.be.server.domain.order;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentProcessor;
import kr.hhplus.be.server.domain.user.UserValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderGenerator orderGenerator;

	private final UserValidator userValidator;

	private final PaymentProcessor paymentProcessor;

	public OrderPayment order(long userId, List<OrderProduct> orderProducts) {

		userValidator.validate(userId);

		Order order = orderGenerator.generate(userId, orderProducts);
		Payment payment = paymentProcessor.process(userId, order);

		return new OrderPayment(order, payment);

	}

	public OrderPayment orderWithCoupon(long userId, long couponId, List<OrderProduct> orderProducts) {

		userValidator.validate(userId);

		Order order = orderGenerator.generate(userId, orderProducts);
		Payment payment = paymentProcessor.processWithCoupon(userId, couponId, order);

		return new OrderPayment(order, payment);

	}
}
