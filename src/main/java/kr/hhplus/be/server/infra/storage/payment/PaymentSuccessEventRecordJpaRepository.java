package kr.hhplus.be.server.infra.storage.payment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.hhplus.be.server.domain.payment.PaymentSuccessEventRecord;

public interface PaymentSuccessEventRecordJpaRepository extends JpaRepository<PaymentSuccessEventRecord, Long> {

	@Query("select e from PaymentSuccessEventRecord e where e.status = kr.hhplus.be.server.enums.PaymentEventRecordStatus.FAILED order by e.orderCreatedAt DESC")
	List<PaymentSuccessEventRecord> findAllFailedRecord();

	Optional<PaymentSuccessEventRecord> findByPaymentId(long paymentId);
}
