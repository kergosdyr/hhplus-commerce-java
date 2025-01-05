package kr.hhplus.be.server.infra.storage.order;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

	private final OrderJpaRepository orderJpaRepository;

	@Override
	public Order save(Order order) {
		return orderJpaRepository.save(order);
	}
}
