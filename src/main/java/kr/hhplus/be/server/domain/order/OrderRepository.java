package kr.hhplus.be.server.domain.order;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public interface OrderRepository {

	Order save(Order order);

	Optional<Order> findById(long orderId);
}
