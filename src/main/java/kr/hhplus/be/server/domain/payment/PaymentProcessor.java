package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.analytics.AnalyticData;
import kr.hhplus.be.server.domain.analytics.AnalyticsSender;
import kr.hhplus.be.server.domain.analytics.AnalyticsService;
import kr.hhplus.be.server.domain.balanace.BalanceModifier;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponLoader;
import kr.hhplus.be.server.domain.order.Order;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentProcessor {

	private final BalanceModifier balanceModifier;

	private final PaymentRepository paymentRepository;

	private final UserCouponLoader userCouponLoader;

	private final AnalyticsService analyticsService;
	private final AnalyticsSender analyticsSender;

	@Transactional
	public Payment process(long userId, Order order) {

		long totalPrice = order.getTotalPrice();
		balanceModifier.use(userId, totalPrice);

		Payment payment = Payment.noCouponBuilder()
			.orderId(order.getOrderId())
			.totalPrice(totalPrice)
			.userId(userId)
			.build();

		Payment savedPayment = paymentRepository.save(payment);
		analyticsSender.send(new AnalyticData(savedPayment.getPaymentId(), order.getOrderId(), order.getCreatedAt()));
		return savedPayment;

	}

	@Transactional
	public Payment processWithCoupon(long userId, long couponId, Order order) {

		long totalPrice = order.getTotalPrice();

		UserCoupon userCoupon = userCouponLoader.load(userId, couponId);
		long couponUsedPrice = userCoupon.use(totalPrice);
		balanceModifier.use(userId, couponUsedPrice);

		Payment payment = Payment.withCouponBuilder()
			.orderId(order.getOrderId())
			.totalPrice(totalPrice)
			.userId(userId)
			.couponUsedPrice(couponUsedPrice)
			.build();

		Payment savedPayment = paymentRepository.save(payment);
		analyticsSender.send(new AnalyticData(savedPayment.getPaymentId(), order.getOrderId(), order.getCreatedAt()));
		return savedPayment;

	}
}
