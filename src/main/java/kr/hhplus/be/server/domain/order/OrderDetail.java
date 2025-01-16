package kr.hhplus.be.server.domain.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderDetail extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long orderDetailId;

	@MapsId("orderId")
	@Column(nullable = false, name = "order_id")
	private long orderId;

	@Column(nullable = false, name = "product_id")
	private long productId;

	@Column(nullable = false)
	private long quantity;

	@Column(nullable = false)
	private long productPrice;

	@Column(nullable = false)
	private long amount;

	@Builder
	public OrderDetail(long orderDetailId, long productPrice, long quantity, long productId, long orderId) {
		this.orderDetailId = orderDetailId;
		this.productPrice = productPrice;
		this.quantity = quantity;
		this.productId = productId;
		this.orderId = orderId;
		this.amount = quantity * productPrice;
	}
}
