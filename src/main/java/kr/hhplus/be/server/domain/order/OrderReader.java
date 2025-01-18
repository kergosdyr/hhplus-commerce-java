package kr.hhplus.be.server.domain.order;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderReader {

	private final OrderRepository orderRepository;

	public Order read(long orderId) {

		return orderRepository.findById(orderId).orElseThrow(() -> new ApiException(ErrorType.ORDER_NOT_FOUND));

	}

}
