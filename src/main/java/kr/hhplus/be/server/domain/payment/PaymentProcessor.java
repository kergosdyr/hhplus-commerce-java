package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional
	public Payment process(long userId, Order order) {

		long totalPrice = order.getTotalPrice();
		balanceModifier.use(userId, totalPrice);

		Payment payment = Payment.noCouponBuilder()
			.orderId(order.getOrderId())
			.totalPrice(totalPrice)
			.userId(userId)
			.build();

		return paymentRepository.save(payment);

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

		return paymentRepository.save(payment);

	}
}
