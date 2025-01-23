package kr.hhplus.be.server.api.request;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.order.OrderCommand;

public record OrderRequest(
	@NotNull(message = "userId는 필수값입니다.")
	@Min(value = 1, message = "userId는 1 이상의 값이어야 합니다.")
	long userId,


	@Min(value = 1, message = "couponId는 1 이상의 값이어야 합니다.")
	Long couponId,

	@NotNull(message = "orderItems는 비어있으면 안 됩니다.")
	@Valid // 각 OrderItem 내부 필드도 검증
	List<OrderItem> orderItems
) {
	public List<OrderCommand> toOrderProducts() {
		return orderItems.stream().map(OrderItem::toOrderProduct).collect(Collectors.toList());
	}

	public boolean isWithCoupon() {
		return couponId != null && couponId > 0;
	}

	public record OrderItem(
		@Min(value = 1, message = "quantity는 1 이상의 값이어야 합니다.")
		long quantity,

		@Min(value = 1, message = "productId는 1 이상의 값이어야 합니다.")
		long productId
	) {
		public OrderCommand toOrderProduct() {
			return new OrderCommand(this.quantity, this.productId);
		}
	}
}
