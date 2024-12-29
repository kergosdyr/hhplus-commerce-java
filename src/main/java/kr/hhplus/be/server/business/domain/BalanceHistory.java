package kr.hhplus.be.server.business.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "balance_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "balance_history_id", nullable = false)
	private Long balanceHistoryId;

	@Column(name = "balance_id", nullable = false)
	private Long balanceId;

	@Column(name = "change_amount", nullable = false)
	private Long changeAmount;

	@Column(name = "transaction_type", length = 50)
	private String transactionType;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;
}
