package kr.hhplus.be.server.infra.storage.payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.payment.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
	List<Payment> findByOrderId(long orderId);
}
