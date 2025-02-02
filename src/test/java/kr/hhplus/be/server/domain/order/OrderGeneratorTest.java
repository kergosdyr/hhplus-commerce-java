package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductReader;

@ExtendWith(MockitoExtension.class)
class OrderGeneratorTest {

	@Mock
	OrderRepository orderRepository;

	@Mock
	ProductReader productReader;



	@InjectMocks
	OrderGenerator orderGenerator;

	@Test
	@DisplayName("generate 를 수행하면 order 를 생성 후 저장하고, orderDetailList 를 생성한 후 저장한다")
	void shouldGenerateOrderWithOrderDetailList() {

		var orderProducts = List.of(
			new OrderCommand(2L, 1L),
			new OrderCommand(1L, 2L));

		when(orderRepository.save(any(Order.class))).thenReturn(
			Order.builder()
				.orderId(1L)
				.userId(1L)
				.orderDetails(List.of(
					OrderDetail.builder()
						.productId(2L)
						.productPrice(1000L)
						.quantity(1L)
						.build(),
					OrderDetail.builder()
						.productId(3L)
						.productPrice(2000L)
						.quantity(1L)
						.build()

				))
				.build()
		);

		when(productReader.read(2L)).thenReturn(
			Product.builder()
				.productId(2L)
				.price(1000L)
				.build()
		);

		when(productReader.read(1L)).thenReturn(
			Product.builder()
				.productId(1L)
				.price(2000L)
				.build()
		);




		var generatedOrder = orderGenerator.generate(1L, orderProducts);

		assertThat(generatedOrder).isNotNull();
		assertThat(generatedOrder.getUserId()).isEqualTo(1L);
		assertThat(generatedOrder.getTotalAmount()).isEqualTo(3000L);


	}

}