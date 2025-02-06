package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.analytics.AnalyticData;
import kr.hhplus.be.server.domain.analytics.AnalyticsSender;
import kr.hhplus.be.server.domain.balanace.BalanceModifier;
import kr.hhplus.be.server.domain.coupon.CouponApplier;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderReader;
import kr.hhplus.be.server.support.WithLock;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentService {

	private final BalanceModifier balanceModifier;

	private final PaymentRepository paymentRepository;

	private final OrderReader orderReader;

	private final AnalyticsSender analyticsSender;

	private final CouponApplier couponApplier;

	@Transactional
	@WithLock(key = "'user_id:'.concat(#userId).concat(':order_id:').concat(#orderId)")
	public PaymentOutput pay(long userId, long orderId) {

		Order order = orderReader.read(orderId);
		order.paid();

		balanceModifier.use(userId, order.getTotalAmount());

		Payment payment = Payment.noCouponBuilder()
			.orderId(orderId)
			.paymentAmount(order.getTotalAmount())
			.userId(userId)
			.build();

		Payment savedPayment = paymentRepository.save(payment);
		analyticsSender.send(new AnalyticData(savedPayment.getPaymentId(), orderId, order.getCreatedAt()));
		return new PaymentOutput(savedPayment, order);

	}

	@Transactional
	@WithLock(key = "'user_id:'.concat(#userId).concat(':order_id:').concat(#orderId)")
	public PaymentOutput payWithCoupon(long userId, long couponId, long orderId) {

		Order order = orderReader.read(orderId);
		order.paid();

		long couponAppliedPrice = couponApplier.apply(order.getTotalAmount(), userId, couponId);
		balanceModifier.use(userId, couponAppliedPrice);

		Payment payment = Payment.withCouponBuilder()
			.orderId(order.getOrderId())
			.paymentAmount(couponAppliedPrice)
			.userId(userId)
			.couponAppliedPrice(couponAppliedPrice)
			.build();

		Payment savedPayment = paymentRepository.save(payment);
		analyticsSender.send(new AnalyticData(savedPayment.getPaymentId(), order.getOrderId(), order.getCreatedAt()));
		return new PaymentOutput(savedPayment, order);

	}
}
