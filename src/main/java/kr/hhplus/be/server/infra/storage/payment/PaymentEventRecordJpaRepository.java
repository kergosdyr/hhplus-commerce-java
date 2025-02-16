package kr.hhplus.be.server.infra.storage.payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.hhplus.be.server.domain.payment.PaymentSuccessEventRecord;

public interface PaymentEventRecordJpaRepository extends JpaRepository<PaymentSuccessEventRecord, Long> {

	@Query("select e from PaymentSuccessEventRecord e where e.status = kr.hhplus.be.server.enums.PaymentEventRecordStatus.READY")
	List<PaymentSuccessEventRecord> findAllByStatusReady();

}
