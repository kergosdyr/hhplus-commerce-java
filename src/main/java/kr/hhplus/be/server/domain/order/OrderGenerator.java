package kr.hhplus.be.server.domain.order;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductReader;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderGenerator {

	private final OrderRepository orderRepository;

	private final ProductReader productReader;


	@Transactional
	public Order generate(long userId, List<OrderProduct> orderProducts) {

		List<OrderDetail> orderDetailList = orderProducts.stream().map(orderProduct -> {

				Product product = productReader.read(orderProduct.productId());

				return OrderDetail.builder()
					.productId(product.getProductId())
				.quantity(orderProduct.quantity())
					.productPrice(product.getPrice())
					.build();

			}
		).toList();

		Order order = Order.builder()
			.userId(userId)
			.orderDetails(orderDetailList)
			.build();

		return orderRepository.save(order);

	}

}
