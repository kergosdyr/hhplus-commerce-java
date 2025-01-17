package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.enums.PaymentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Payment extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long paymentId;

	@Column(nullable = false)
	private long orderId;

	@Column(nullable = false)
	private long userId;

	@Column(nullable = false)
	private boolean isUsedCoupon;

	@Column(nullable = false)
	private long paymentAmount;

	@Column
	private long couponAppliedPrice;

	@Column
	private long couponId;

	@Column(nullable = false, length = 50)
	@Builder.Default
	private PaymentStatus status = PaymentStatus.PAID;

	@Builder(builderMethodName = "noCouponBuilder")
	private Payment(long orderId, long userId, long paymentAmount) {
		this.orderId = orderId;
		this.userId = userId;
		this.paymentAmount = paymentAmount;
	}

	@Builder(builderMethodName = "withCouponBuilder")
	public Payment(long orderId, long userId, long couponAppliedPrice, long paymentAmount) {
		this.orderId = orderId;
		this.userId = userId;
		this.couponAppliedPrice = couponAppliedPrice;
		this.paymentAmount = paymentAmount;
	}
}
