package kr.hhplus.be.server.domain.order;

import org.springframework.stereotype.Component;

@Component
public interface OrderRepository {

	Order save(Order order);
}
