package kr.hhplus.be.server.domain.order;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.ProductStockModifier;
import kr.hhplus.be.server.domain.user.UserFinder;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import kr.hhplus.be.server.support.WithLock;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderGenerator orderGenerator;

	private final UserFinder userFinder;

	private final PaymentService paymentService;

	private final ProductStockModifier productStockModifier;

	@WithLock(key = "#orderCommands.![ 'order:product:' + productId ]")
	public OrderOutput order(long userId, List<OrderCommand> orderCommands) {

		if (userFinder.notExistsByUserId(userId)) {
			throw new ApiException(ErrorType.USER_NOT_FOUND);
		}

		var order = orderGenerator.generate(userId, orderCommands);
		productStockModifier.sell(orderCommands);
		var paymentProcessOutput = paymentService.pay(userId, order.getOrderId());

		return new OrderOutput(paymentProcessOutput.order(), paymentProcessOutput.payment());

	}

	public OrderOutput orderWithCoupon(long userId, long couponId, List<OrderCommand> orderCommands) {

		if (userFinder.notExistsByUserId(userId)) {
			throw new ApiException(ErrorType.USER_NOT_FOUND);
		}

		var order = orderGenerator.generate(userId, orderCommands);
		productStockModifier.sell(orderCommands);
		var paymentProcessOutput = paymentService.payWithCoupon(userId, couponId, order.getOrderId());

		return new OrderOutput(paymentProcessOutput.order(), paymentProcessOutput.payment());

	}
}
