package kr.hhplus.be.server.api.request;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.order.OrderProduct;

public record OrderRequest(
	@NotNull(message = "userId는 필수값입니다.")
	@Min(value = 1, message = "userId는 1 이상의 값이어야 합니다.")
	long userId,

	// 쿠폰 ID가 Optional 이라면, NotNull을 제거하고 @Positive(or @Min)만 달 수도 있음
	@Min(value = 1, message = "couponId는 1 이상의 값이어야 합니다.")
	long couponId,

	@NotNull(message = "orderItems는 비어있으면 안 됩니다.")
	@Valid // 각 OrderItem 내부 필드도 검증
	List<OrderItem> orderItems
) {
	public List<OrderProduct> toOrderProducts() {
		return orderItems.stream().map(OrderItem::toOrderProduct).collect(Collectors.toList());
	}

	public record OrderItem(
		@Min(value = 1, message = "quantity는 1 이상의 값이어야 합니다.")
		long quantity,

		@Min(value = 1, message = "productId는 1 이상의 값이어야 합니다.")
		long productId
	) {
		public OrderProduct toOrderProduct() {
			return new OrderProduct(this.quantity, this.productId);
		}
	}
}