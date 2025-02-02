package kr.hhplus.be.server.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.analytics.AnalyticData;
import kr.hhplus.be.server.domain.analytics.AnalyticsSender;
import kr.hhplus.be.server.domain.analytics.AnalyticsService;
import kr.hhplus.be.server.domain.balanace.BalanceModifier;
import kr.hhplus.be.server.domain.coupon.CouponApplier;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponFinder;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderReader;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorTest {

	@Mock
	private BalanceModifier balanceModifier;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private UserCouponFinder userCouponFinder;

	@Mock
	private Order mockOrder;

	@Mock
	private UserCoupon mockUserCoupon;

	@Mock
	private AnalyticsService analyticsService;

	@Mock
	private AnalyticsSender analyticsSender;

	@Mock
	private CouponApplier couponApplier;


	@InjectMocks
	private PaymentProcessor paymentProcessor;

	@Mock
	private OrderReader orderReader;

	@Test
	@DisplayName("process() 호출 시 쿠폰 없이 결제를 진행하고, Payment를 저장 후 반환한다.")
	void shouldProcessPaymentWithoutCoupon() {
		// given
		long userId = 1L;
		long orderId = 10L;
		long totalPrice = 3000L;

		when(mockOrder.getOrderId()).thenReturn(orderId);
		when(mockOrder.getTotalAmount()).thenReturn(totalPrice);

		Payment savedPayment = Payment.builder()
			.paymentId(1L)
			.orderId(orderId)
			.userId(userId)
			.paymentAmount(totalPrice)
			.build();

		when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
		when(analyticsSender.send(any(AnalyticData.class))).thenReturn(true);
		when(orderReader.read(orderId)).thenReturn(mockOrder);

		// when
		var result = paymentProcessor.process(userId, mockOrder.getOrderId());

		// then
		verify(balanceModifier, times(1)).use(userId, totalPrice);
		ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
		verify(paymentRepository, times(1)).save(paymentCaptor.capture());

		Payment capturedPayment = paymentCaptor.getValue();
		assertThat(capturedPayment.getUserId()).isEqualTo(userId);
		assertThat(capturedPayment.getOrderId()).isEqualTo(orderId);
		assertThat(capturedPayment.getPaymentAmount()).isEqualTo(totalPrice);

		assertThat(result.payment()).isEqualTo(savedPayment);
	}

	@Test
	@DisplayName("processWithCoupon() 호출 시, UserCoupon을 로딩 후 쿠폰 사용 금액만큼 Balance를 차감하고 Payment를 반환한다.")
	void shouldProcessPaymentWithCoupon() {
		// given
		long userId = 2L;
		long couponId = 100L;
		long orderId = 20L;
		long totalPrice = 5000L;
		long couponAppliedPrice = 2000L;

		when(mockOrder.getOrderId()).thenReturn(orderId);
		when(mockOrder.getTotalAmount()).thenReturn(totalPrice);

		Payment savedPayment = Payment.builder()
			.paymentId(888L)
			.orderId(orderId)
			.userId(userId)
			.paymentAmount(couponAppliedPrice)
			.couponAppliedPrice(couponAppliedPrice)
			.build();

		when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
		when(analyticsSender.send(any(AnalyticData.class))).thenReturn(true);
		when(couponApplier.apply(any(Long.class), any(Long.class), any(Long.class))).thenReturn(couponAppliedPrice);
		when(orderReader.read(orderId)).thenReturn(mockOrder);

		// when
		var result = paymentProcessor.processWithCoupon(userId, couponId, mockOrder.getOrderId());

		verify(balanceModifier, times(1)).use(userId, couponAppliedPrice);

		ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
		verify(paymentRepository, times(1)).save(paymentCaptor.capture());

		Payment capturedPayment = paymentCaptor.getValue();
		assertThat(capturedPayment.getOrderId()).isEqualTo(orderId);
		assertThat(capturedPayment.getUserId()).isEqualTo(userId);
		assertThat(capturedPayment.getPaymentAmount()).isEqualTo(couponAppliedPrice);
		assertThat(capturedPayment.getCouponAppliedPrice()).isEqualTo(couponAppliedPrice);
		assertThat(result.payment()).isEqualTo(savedPayment);
	}

}