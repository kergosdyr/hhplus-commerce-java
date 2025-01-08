package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Coupon extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_id")
	private long couponId;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(nullable = false)
	private long amount;

	@Column(nullable = false, length = 50)
	private String status;

	@Column(nullable = false)
	private LocalDateTime expiredAt;

	@OneToOne
	@JoinColumn(name = "coupon_id", referencedColumnName = "coupon_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private CouponInventory couponInventory;

	public boolean isIssuable(LocalDateTime issuedAt) {
		return !expiredAt.isBefore(issuedAt);
	}
}
