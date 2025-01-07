package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.domain.product.Product;

class OrderGeneratorIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("userId와 OrderProduct 목록이 주어지면 Order와 OrderDetail이 생성된다")
	void shouldCreateOrderAndOrderDetailsGivenUserIdAndOrderProductList() {
		// given
		long userId = 1L;

		List<Product> savedProducts = productJpaRepository.saveAll(List.of(Product.builder()
			.price(1000L)
			.name("전진 상품1")
			.status("VALID")
			.build(), Product.builder()
			.price(2000L)
			.name("전진 상품2")
			.status("VALID")
			.build()));

		List<OrderProduct> orderProducts = savedProducts.stream()
			.map(x -> new OrderProduct(1L, x.getProductId()))
			.toList();

		// when
		Order createdOrder = orderGenerator.generate(userId, orderProducts);
		List<OrderDetail> orderDetails = orderDetailJpaRepository.findByOrderId(createdOrder.getOrderId());

		// then
		assertThat(createdOrder).isNotNull();
		assertThat(createdOrder.getUserId()).isEqualTo(userId);
		assertThat(createdOrder.getTotal()).isEqualTo(orderProducts.size());

		assertThat(orderDetails).hasSize(orderProducts.size());
		assertThat(orderDetails)
			.hasSize(orderProducts.size())
			.allSatisfy(orderDetail ->
				assertThat(orderDetail.getOrderId()).isEqualTo(createdOrder.getOrderId())
			);

		assertThat(orderDetails)
			.zipSatisfy(orderProducts, (orderDetail, orderProduct) -> {
				assertThat(orderDetail.getProductId()).isEqualTo(orderProduct.productId());
				assertThat(orderDetail.getQuantity()).isEqualTo(orderProduct.quantity());
			});
	}

}