package kr.hhplus.be.server.domain.payment;

import static kr.hhplus.be.server.config.TestUtil.createTestCoupon;
import static kr.hhplus.be.server.config.TestUtil.createTestUser;
import static kr.hhplus.be.server.config.TestUtil.getTestBalance;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import kr.hhplus.be.server.domain.order.OrderDetail;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.enums.OrderStatus;

class PaymentProcessorIntegrationTest extends IntegrationTest {

	public static Order createMockOrder(long userId) {
		Product product1 = Product.builder()
			.productId(1L)
			.name("Product A")
			.price(5000L)
			.status("AVAILABLE")
			.build();

		Product product2 = Product.builder()
			.productId(2L)
			.name("Product B")
			.price(10000L)
			.status("AVAILABLE")
			.build();

		OrderDetail orderDetail1 = OrderDetail.builder()
			.orderDetailId(1L)
			.orderId(1L)
			.productId(product1.getProductId())
			.quantity(1L) // 1 * 5000
			.product(product1)
			.build();

		OrderDetail orderDetail2 = OrderDetail.builder()
			.orderDetailId(2L)
			.orderId(1L)
			.productId(product2.getProductId())
			.quantity(1L) // 1 * 10000
			.product(product2)
			.build();

		List<OrderDetail> orderDetails = new ArrayList<>();
		orderDetails.add(orderDetail1);
		orderDetails.add(orderDetail2);

		return Order.builder()
			.orderId(1L)
			.userId(userId)
			.total(orderDetails.size())
			.status(OrderStatus.UNPAID)
			.orderDetails(orderDetails)
			.build();
	}

	@Test
	@DisplayName("process()를 호출하면 쿠폰 없이 Payment가 정상적으로 생성, 저장되고, balance 가 Order의 totalAmount 인 15000L 만큼 감소한다")
	void shouldProcessPaymentWithoutCoupon() {

		User user = createTestUser();
		User savedUser = userJpaRepository.save(user);
		Balance balance = getTestBalance(savedUser.getUserId(), 15000L);
		Balance savedBalance = balanceJpaRepository.save(balance);
		Order mockOrder = createMockOrder(savedUser.getUserId());
		Payment payment = paymentProcessor.process(savedUser.getUserId(), mockOrder);
		Payment savedPayment = paymentJpaRepository.findById(payment.getPaymentId()).orElseThrow(RuntimeException::new);
		Balance modifiedBalance = balanceJpaRepository.findById(savedBalance.getBalanceId())
			.orElseThrow(RuntimeException::new);

		assertThat(payment.getPaymentId()).isEqualTo(savedPayment.getPaymentId());
		assertThat(payment.getTotalPrice()).isEqualTo(mockOrder.getTotalPrice());
		assertThat(modifiedBalance.getAmount()).isEqualTo(0L);

	}

	@Test
	@DisplayName("processWithCoupon()를 호출하면 쿠폰 사용 금액인 1000L 만큼 Payment에 반영되고, 실제로는 14000L 만큼 사용되어 DB에 저장된다.")
	void shouldProcessPaymentWithCoupon() {

		User user = createTestUser();
		User savedUser = userJpaRepository.save(user);
		Balance balance = getTestBalance(savedUser.getUserId(), 15000L);
		Balance savedBalance = balanceJpaRepository.save(balance);
		Order mockOrder = createMockOrder(savedUser.getUserId());

		Coupon coupon = createTestCoupon(LocalDateTime.of(2024, 1, 2, 0, 0));
		Coupon savedCoupon = couponJpaRepository.save(coupon);
		CouponInventory couponInventory = TestUtil.createTestCouponInventory(savedCoupon.getCouponId(), 10L);
		CouponInventory savedCouponInventory = couponInventoryJpaRepository.save(couponInventory);
		UserCoupon userCoupon = TestUtil.createTestUserCoupon(savedUser.getUserId(), savedCoupon.getCouponId());
		UserCoupon savedUserCoupon = userCouponJpaRepository.save(userCoupon);

		Payment payment = paymentProcessor.processWithCoupon(savedUser.getUserId(), savedCoupon.getCouponId(),
			mockOrder);
		Payment savedPayment = paymentJpaRepository.findById(payment.getPaymentId()).orElseThrow(RuntimeException::new);
		Balance modifiedBalance = balanceJpaRepository.findById(savedBalance.getBalanceId())
			.orElseThrow(RuntimeException::new);

		assertThat(payment.getPaymentId()).isEqualTo(savedPayment.getPaymentId());
		assertThat(payment.getTotalPrice()).isEqualTo(mockOrder.getTotalPrice());
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

		Order mockOrder = createMockOrder(savedUser.getUserId());

		int numberOfRequests = 5;
		var run = ConcurrencyTestUtil.run(numberOfRequests, () -> {
			try {
				paymentProcessor.process(savedUser.getUserId(), mockOrder);
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