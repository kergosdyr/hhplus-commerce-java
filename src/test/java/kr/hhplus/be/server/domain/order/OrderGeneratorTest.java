package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderGeneratorTest {

	@Mock
	OrderRepository orderRepository;

	@Mock
	OrderDetailRepository orderDetailRepository;

	@InjectMocks
	OrderGenerator orderGenerator;

	@Test
	@DisplayName("generate 를 수행하면 order 를 생성 후 저장하고, orderDetailList 를 생성한 후 저장한다")
	void shouldGenerateOrderWithOrderDetailList() {

		var orderProducts = List.of(
			new OrderProduct(2L, 1L),
			new OrderProduct(1L, 2L));

		when(orderRepository.save(any(Order.class))).thenReturn(
			Order.builder()
				.orderId(1L)
				.userId(1L)
				.total(2L)
				.build()
		);

		var generatedOrder = orderGenerator.generate(1L, orderProducts);

		verify(orderRepository, times(1)).save(any(Order.class));
		verify(orderDetailRepository, times(1)).save(argThat(
			list -> list != null && list.size() == 2
		));

		assertThat(generatedOrder).isNotNull();
		assertThat(generatedOrder.getTotal()).isEqualTo(2L);

	}

}