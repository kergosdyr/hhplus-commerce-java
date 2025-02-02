package kr.hhplus.be.server.domain.payment;

import static kr.hhplus.be.server.config.TestUtil.createMockOrderDetails;
import static kr.hhplus.be.server.config.TestUtil.createTestCoupon;
import static kr.hhplus.be.server.config.TestUtil.createTestUser;
import static kr.hhplus.be.server.config.TestUtil.getTestBalance;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.config.ConcurrencyTestUtil;
import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.domain.balanace.Balance;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponInventory;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.user.User;

class PaymentProcessorIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("process()를 호출하면 쿠폰 없이 Payment가 정상적으로 생성, 저장되고, balance 가 Order의 totalAmount 인 15000L 만큼 감소한다")
	void shouldProcessPaymentWithoutCoupon() {

		User user = createTestUser();
		User savedUser = userJpaRepository.save(user);
		Balance balance = getTestBalance(savedUser.getUserId(), 15000L);
		Balance savedBalance = balanceJpaRepository.save(balance);
		Order mockOrder = orderJpaRepository.save(
			TestUtil.createMockOrder(savedUser.getUserId(), createMockOrderDetails(3, 5000L, 1)));
		var paymentProcessOutput = paymentProcessor.process(savedUser.getUserId(), mockOrder.getOrderId());
		Payment payment = paymentProcessOutput.payment();
		Payment savedPayment = paymentJpaRepository.findById(payment.getPaymentId()).orElseThrow(RuntimeException::new);
		Balance modifiedBalance = balanceJpaRepository.findById(savedBalance.getBalanceId())
			.orElseThrow(RuntimeException::new);

		assertThat(payment.getPaymentId()).isEqualTo(savedPayment.getPaymentId());
		assertThat(payment.getPaymentAmount()).isEqualTo(mockOrder.getTotalAmount());
		assertThat(modifiedBalance.getAmount()).isEqualTo(0L);

	}

	@Test
	@DisplayName("processWithCoupon()를 호출하면 쿠폰 사용 금액인 1000L 만큼 Payment에 반영되고, 실제로는 14000L 만큼 사용되어 DB에 저장된다.")
	void shouldProcessPaymentWithCoupon() {

		User user = createTestUser();
		User savedUser = userJpaRepository.save(user);
		Balance balance = getTestBalance(savedUser.getUserId(), 15000L);
		Balance savedBalance = balanceJpaRepository.save(balance);
		Order savedMockOrder = orderJpaRepository.save(
			TestUtil.createMockOrder(savedUser.getUserId(), createMockOrderDetails(3, 5000L, 1)));

		Coupon coupon = createTestCoupon(LocalDateTime.of(2024, 1, 2, 0, 0));
		Coupon savedCoupon = couponJpaRepository.save(coupon);
		CouponInventory couponInventory = TestUtil.createTestCouponInventory(savedCoupon.getCouponId(), 10L);
		CouponInventory savedCouponInventory = couponInventoryJpaRepository.save(couponInventory);
		UserCoupon userCoupon = TestUtil.createTestUserCoupon(savedUser.getUserId(), savedCoupon.getCouponId());
		UserCoupon savedUserCoupon = userCouponJpaRepository.save(userCoupon);

		var paymentProcessOutput = paymentProcessor.processWithCoupon(savedUser.getUserId(), savedCoupon.getCouponId(),
			savedMockOrder.getOrderId());
		Payment payment = paymentProcessOutput.payment();
		Payment savedPayment = paymentJpaRepository.findById(payment.getPaymentId()).orElseThrow(RuntimeException::new);
		Balance modifiedBalance = balanceJpaRepository.findById(savedBalance.getBalanceId())
			.orElseThrow(RuntimeException::new);

		assertThat(payment.getPaymentId()).isEqualTo(savedPayment.getPaymentId());
		assertThat(payment.getPaymentAmount()).isEqualTo(14000L);
		assertThat(modifiedBalance.getAmount()).isEqualTo(1000L);

	}

	@Test
	@DisplayName("유저의 balance가 15000원이고 15000원 결제를 5번 동시 요청 시도할 경우 1번만 성공해야 한다.")
	void shouldProcessOnlyFourPaymentsWhen40ConcurrentRequestsAreMade() throws InterruptedException {
		// given
		User user = createTestUser();
		User savedUser = userJpaRepository.save(user);

		Balance balance = getTestBalance(savedUser.getUserId(), 15_000L);
		Balance savedBalance = balanceJpaRepository.save(balance);
		Order mockOrder = orderJpaRepository.save(
			TestUtil.createMockOrder(savedUser.getUserId(), createMockOrderDetails(3, 5000L, 1)));


		int numberOfRequests = 5;
		var run = ConcurrencyTestUtil.run(numberOfRequests, () -> {
			try {
				paymentProcessor.process(savedUser.getUserId(), mockOrder.getOrderId());

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		});

		assertThat(run.success()).isEqualTo(1);
		assertThat(run.fail()).isEqualTo(4);

		// 5) DB에 반영된 최종 balance가 0원인지 확인
		Balance modifiedBalance = balanceJpaRepository
			.findById(savedBalance.getBalanceId())
			.orElseThrow(RuntimeException::new);

		assertThat(modifiedBalance.getAmount()).isEqualTo(0L);
	}

}