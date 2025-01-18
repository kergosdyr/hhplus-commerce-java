package kr.hhplus.be.server.domain.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentProcessor;
import kr.hhplus.be.server.domain.product.ProductStockModifier;
import kr.hhplus.be.server.domain.user.UserFinder;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderGenerator orderGenerator;

	private final UserFinder userFinder;

	private final PaymentProcessor paymentProcessor;

	private final ProductStockModifier productStockModifier;

	@Transactional
	public OrderPayment order(long userId, List<OrderProduct> orderProducts) {

		if (userFinder.notExistsByUserId(userId)) {
			throw new ApiException(ErrorType.USER_NOT_FOUND);
		}

		Order order = orderGenerator.generate(userId, orderProducts);
		productStockModifier.sell(orderProducts);
		Payment payment = paymentProcessor.process(userId, order.getOrderId());

		return new OrderPayment(order, payment);

	}

	@Transactional
	public OrderPayment orderWithCoupon(long userId, long couponId, List<OrderProduct> orderProducts) {

		if (userFinder.notExistsByUserId(userId)) {
			throw new ApiException(ErrorType.USER_NOT_FOUND);
		}

		Order order = orderGenerator.generate(userId, orderProducts);
		productStockModifier.sell(orderProducts);
		Payment payment = paymentProcessor.processWithCoupon(userId, couponId, order.getOrderId());

		return new OrderPayment(order, payment);

	}
}
