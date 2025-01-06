package kr.hhplus.be.server.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.hhplus.be.server.infra.storage.coupon.CouponInventoryJpaRepository;
import kr.hhplus.be.server.infra.storage.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infra.storage.order.OrderJpaRepository;
import kr.hhplus.be.server.infra.storage.payment.PaymentJpaRepository;
import kr.hhplus.be.server.infra.storage.product.ProductJpaRepository;

@SpringBootTest
public class IntegrationTest {

	@Autowired
	protected CouponInventoryJpaRepository couponInventoryJpaRepository;

	@Autowired
	protected CouponJpaRepository couponJpaRepository;

	@Autowired
	protected OrderJpaRepository orderJpaRepository;

	@Autowired
	protected PaymentJpaRepository paymentJpaRepository;

	@Autowired
	protected ProductJpaRepository productJpaRepository;

	@BeforeEach
	void init() {
		couponInventoryJpaRepository.deleteAllInBatch();
		couponJpaRepository.deleteAllInBatch();
		orderJpaRepository.deleteAllInBatch();
		paymentJpaRepository.deleteAllInBatch();
		productJpaRepository.deleteAllInBatch();
	}
}
