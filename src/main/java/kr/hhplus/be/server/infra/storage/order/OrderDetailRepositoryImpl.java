package kr.hhplus.be.server.infra.storage.order;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.order.OrderDetail;
import kr.hhplus.be.server.domain.order.OrderDetailRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderDetailRepositoryImpl implements OrderDetailRepository {

	private final OrderDetailJpaRepository orderDetailJpaRepository;

	@Override
	public List<OrderDetail> save(List<OrderDetail> orderDetails) {
		return orderDetailJpaRepository.saveAll(orderDetails);
	}
}
