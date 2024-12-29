package kr.hhplus.be.server.business.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id", nullable = false)
	private Long orderId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "user_coupon_id")
	private Long userCouponId;

	@Column(name = "total_amount", nullable = false)
	private int totalAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 50)
	private OrderStatus status;  // PENDING, COMPLETED, CANCELLED

	@Column(name = "order_date")
	private LocalDateTime orderDate;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;
}
