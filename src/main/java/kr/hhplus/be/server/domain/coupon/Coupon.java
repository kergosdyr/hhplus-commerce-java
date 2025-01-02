package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long couponId;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(nullable = false)
	private Long amount;

	@Column(nullable = false)
	private Long quantity;

	@Column(nullable = false, length = 50)
	private String status;

	@Column(nullable = false)
	private LocalDateTime expiredAt;
}
