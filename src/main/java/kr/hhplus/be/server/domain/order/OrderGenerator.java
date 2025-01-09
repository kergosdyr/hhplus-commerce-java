package kr.hhplus.be.server.domain.order;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderGenerator {

	private final OrderRepository orderRepository;

	private final OrderDetailRepository orderDetailRepository;

	@Transactional
	public Order generate(long userId, List<OrderProduct> orderProducts) {

		Order order = orderRepository.save(Order.builder()
			.userId(userId)
			.total(orderProducts.size())
			.build());

		List<OrderDetail> orderDetailList = orderProducts.stream().map(orderProduct ->
			OrderDetail.builder()
				.orderId(order.getOrderId())
				.productId(orderProduct.productId())
				.quantity(orderProduct.quantity())
				.build()
		).toList();

		orderDetailRepository.save(orderDetailList);
		return order;

	}

}
