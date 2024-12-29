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
import kr.hhplus.be.server.enums.UserCouponStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_coupon_id", nullable = false)
	private Long userCouponId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "coupon_id", nullable = false)
	private Long couponId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 50)
	private UserCouponStatus status;

	@Column(name = "expired_at")
	private LocalDateTime expiredAt;

	@Column(name = "issued_at")
	private LocalDateTime issuedAt;

	@Column(name = "used_at")
	private LocalDateTime usedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;
}
