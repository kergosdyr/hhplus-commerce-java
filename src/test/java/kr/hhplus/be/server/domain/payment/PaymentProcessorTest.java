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
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponLoader;
import kr.hhplus.be.server.domain.order.Order;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorTest {

	@Mock
	private BalanceModifier balanceModifier;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private UserCouponLoader userCouponLoader;

	@Mock
	private Order mockOrder;

	@Mock
	private UserCoupon mockUserCoupon;

	@Mock
	private AnalyticsService analyticsService;

	@Mock
	private AnalyticsSender analyticsSender;

	@InjectMocks
	private PaymentProcessor paymentProcessor;

	@Test
	@DisplayName("process() 호출 시 쿠폰 없이 결제를 진행하고, Payment를 저장 후 반환한다.")
	void shouldProcessPaymentWithoutCoupon() {
		// given
		long userId = 1L;
		long orderId = 10L;
		long totalPrice = 3000L;

		when(mockOrder.getOrderId()).thenReturn(orderId);
		when(mockOrder.getTotalPrice()).thenReturn(totalPrice);

		Payment savedPayment = Payment.builder()
			.paymentId(1L)
			.orderId(orderId)
			.userId(userId)
			.totalPrice(totalPrice)
			.build();

		when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
		when(analyticsSender.send(any(AnalyticData.class))).thenReturn(true);

		// when
		Payment result = paymentProcessor.process(userId, mockOrder);

		// then
		verify(balanceModifier, times(1)).use(userId, totalPrice);
		ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
		verify(paymentRepository, times(1)).save(paymentCaptor.capture());

		Payment capturedPayment = paymentCaptor.getValue();
		assertThat(capturedPayment.getUserId()).isEqualTo(userId);
		assertThat(capturedPayment.getOrderId()).isEqualTo(orderId);
		assertThat(capturedPayment.getTotalPrice()).isEqualTo(totalPrice);

		assertThat(result).isEqualTo(savedPayment);
	}

	@Test
	@DisplayName("processWithCoupon() 호출 시, UserCoupon을 로딩 후 쿠폰 사용 금액만큼 Balance를 차감하고 Payment를 반환한다.")
	void shouldProcessPaymentWithCoupon() {
		// given
		long userId = 2L;
		long couponId = 100L;
		long orderId = 20L;
		long totalPrice = 5000L;

		when(mockOrder.getOrderId()).thenReturn(orderId);
		when(mockOrder.getTotalPrice()).thenReturn(totalPrice);
		when(mockUserCoupon.use(totalPrice)).thenReturn(2000L);

		when(userCouponLoader.load(userId, couponId)).thenReturn(mockUserCoupon);

		Payment savedPayment = Payment.builder()
			.paymentId(888L)
			.orderId(orderId)
			.userId(userId)
			.totalPrice(totalPrice)
			.couponUsedPrice(2000L)
			.build();
		when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
		when(analyticsSender.send(any(AnalyticData.class))).thenReturn(true);

		// when
		Payment result = paymentProcessor.processWithCoupon(userId, couponId, mockOrder);

		// then
		verify(userCouponLoader, times(1)).load(userId, couponId);

		verify(mockUserCoupon, times(1)).use(totalPrice);

		verify(balanceModifier, times(1)).use(userId, 2000L);

		ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
		verify(paymentRepository, times(1)).save(paymentCaptor.capture());

		Payment capturedPayment = paymentCaptor.getValue();
		assertThat(capturedPayment.getOrderId()).isEqualTo(orderId);
		assertThat(capturedPayment.getUserId()).isEqualTo(userId);
		assertThat(capturedPayment.getTotalPrice()).isEqualTo(totalPrice);
		assertThat(capturedPayment.getCouponUsedPrice()).isEqualTo(2000L);
		assertThat(result).isEqualTo(savedPayment);
	}

}