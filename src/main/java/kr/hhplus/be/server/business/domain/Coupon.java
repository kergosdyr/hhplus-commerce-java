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
import kr.hhplus.be.server.enums.CouponStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_id", nullable = false)
	private Long couponId;

	@Column(name = "coupon_name", length = 100, nullable = false)
	private String couponName;

	@Column(name = "discount_value", nullable = false)
	private int discountValue;

	@Column(name = "available_count")
	private Long availableCount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 50)
	private CouponStatus status;  // ACTIVE, INACTIVE

	@Column(name = "is_percent", nullable = false)
	private boolean isPercent;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;
}
