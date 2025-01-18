package kr.hhplus.be.server.domain.order;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_master")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Order extends BaseEntity {
	@Id
	@Column(name = "order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long orderId;

	@Column(nullable = false)
	private long userId;

	@Column(nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private OrderStatus status = OrderStatus.UNPAID;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "order_id", referencedColumnName = "order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	@Builder.Default
	private List<OrderDetail> orderDetails = new ArrayList<>();

	public long getTotalAmount() {

		return this.orderDetails.stream()
			.reduce(0L,
				(sum, orderDetail) -> sum + orderDetail.getAmount(),
				Long::sum);

	}

	public void paid() {
		this.status = OrderStatus.PAID;
	}
}
