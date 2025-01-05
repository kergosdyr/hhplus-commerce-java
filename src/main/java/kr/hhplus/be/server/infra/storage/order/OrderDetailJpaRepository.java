package kr.hhplus.be.server.infra.storage.order;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.order.OrderDetail;

public interface OrderDetailJpaRepository extends JpaRepository<OrderDetail, Long> {
}
