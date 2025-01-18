package kr.hhplus.be.server.infra.storage.order;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.order.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

	@EntityGraph(attributePaths = {"orderDetails"})
	Optional<Order> findById(Long id);

}
