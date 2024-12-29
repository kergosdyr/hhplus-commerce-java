package kr.hhplus.be.server.business.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "balance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Balance extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "balance_id", nullable = false)
	private Long balanceId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "balance_amount", nullable = false)
	private Long balanceAmount;
}
