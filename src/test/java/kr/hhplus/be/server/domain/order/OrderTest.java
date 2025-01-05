package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.product.Product;

@ExtendWith(MockitoExtension.class)
class OrderTest {

	private static List<OrderDetail> createOrderDetails() {
		Product testProduct = Product.builder()
			.productId(1L)
			.price(2000L)
			.build();
		return List.of(OrderDetail.builder()
				.orderDetailId(1L)
				.orderId(1L)
				.quantity(2L)
				.product(testProduct)
				.build(),
			OrderDetail.builder()
				.orderDetailId(2L)
				.orderId(1L)
				.quantity(3L)
				.product(testProduct)
				.build()
		);
	}

	@Test
	@DisplayName("Order 에서 getTotalPrice 를 호출했을 때 갖고있는 OrderDetails 의 Quantity 와 Price 를 곱한 값을 리턴한다")
	void shouldReturnTotalPriceWhenGetTotalPriceCalled() {
		//given

		Order givenOrder = Order.builder()
			.orderId(1L)
			.orderDetails(createOrderDetails())
			.build();

		//when
		long totalPrice = givenOrder.getTotalPrice();

		//then
		assertThat(totalPrice).isEqualTo(2000L * 2L + 2000L * 3L);
	}

}