package kr.hhplus.be.server.domain.order;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public interface OrderDetailRepository {
	List<OrderDetail> save(List<OrderDetail> orderDetails);
}
