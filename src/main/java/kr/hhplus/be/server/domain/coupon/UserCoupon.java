package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.enums.UserCouponStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class UserCoupon extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userCouponId;

	@Column(nullable = false)
	private long userId;

	@Column(nullable = false, name = "coupon_id")
	private long couponId;

	@Column(nullable = false)
	private long amount;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private UserCouponStatus status;

	@Column(nullable = false)
	private LocalDateTime issuedAt;

	@Column(nullable = false)
	private LocalDateTime expiredAt;

	@Column(nullable = false)
	private boolean isUsed;

	@ManyToOne
	@JoinColumn(name = "coupon_id", referencedColumnName = "coupon_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Coupon coupon;

	public long use(long totalPrice) {
		this.status = UserCouponStatus.USED;
		this.isUsed = true;
		return Math.max(totalPrice - this.amount, 0);
	}

}
