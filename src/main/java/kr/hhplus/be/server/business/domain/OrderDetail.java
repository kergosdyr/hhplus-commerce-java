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
import kr.hhplus.be.server.enums.OrderDetailStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_detail_id", nullable = false)
	private Long orderDetailId;

	@Column(name = "order_id", nullable = false)
	private Long orderId;

	@Column(name = "product_id", nullable = false)
	private Long productId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 50)
	private OrderDetailStatus status; // PREPARE, SHIPPING, DELIVERED, CANCELLED

	@Column(name = "quantity", nullable = false)
	private int quantity;

	@Column(name = "item_price", nullable = false)
	private int itemPrice;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;
}
