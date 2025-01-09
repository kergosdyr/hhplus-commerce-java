package kr.hhplus.be.server.infra.storage.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.order.OrderDetail;

public interface OrderDetailJpaRepository extends JpaRepository<OrderDetail, Long> {
	List<OrderDetail> findByOrderId(long orderId);
}
