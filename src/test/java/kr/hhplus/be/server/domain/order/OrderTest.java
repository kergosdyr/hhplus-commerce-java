package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.enums.OrderStatus;

@ExtendWith(MockitoExtension.class)
class OrderTest {

	private static List<OrderDetail> createOrderDetails() {
		return List.of(OrderDetail.builder()
				.orderDetailId(1L)
				.orderId(1L)
				.productPrice(1000L)
				.quantity(2L)
				.build(),
			OrderDetail.builder()
				.orderDetailId(2L)
				.orderId(1L)
				.productPrice(1000L)
				.quantity(3L)
				.build()
		);
	}

	@Test
	@DisplayName("Order 에서 getTotalPrice 를 호출했을 때 갖고있는 OrderDetails 의 Quantity 와 Price 를 곱한 값을 리턴한다")
	void shouldReturnTotalPriceWhenGetTotalAmountCalled() {
		//given

		Order givenOrder = Order.builder()
			.orderId(1L)
			.orderDetails(createOrderDetails())
			.build();

		//when
		long totalPrice = givenOrder.getTotalAmount();

		//then
		assertThat(totalPrice).isEqualTo(5000L);
	}

	@Test
	@DisplayName("Order의 paid 를 호출했을 때 Order 의 상태가 PAID 상태로 변경된다")
	void shouldChangeOrderStatusToPaidWhenPaidCalled() {
		//given
		Order givenOrder = Order.builder()
			.orderId(1L)
			.userId(1L)
			.orderDetails(createOrderDetails())
			.build();

		//when
		givenOrder.paid();

		//then
		assertThat(givenOrder.getStatus()).isEqualTo(OrderStatus.PAID);
	}

}