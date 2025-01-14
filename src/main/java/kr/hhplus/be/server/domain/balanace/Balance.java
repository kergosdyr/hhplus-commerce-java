package kr.hhplus.be.server.domain.balanace;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "balance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Balance extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long balanceId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long amount;

	public void charge(Long amount) {
		this.amount += amount;
	}

	public boolean isUsable(long amount) {
		return this.amount - amount >= 0;
	}

	public void use(long amount) {
		this.amount -= amount;
	}
}
