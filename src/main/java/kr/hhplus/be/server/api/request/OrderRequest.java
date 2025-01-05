package kr.hhplus.be.server.api.request;

import java.util.List;
import java.util.stream.Collectors;

import kr.hhplus.be.server.domain.order.OrderProduct;

public record OrderRequest(long userId, long couponId, List<OrderItem> orderItems) {

	public List<OrderProduct> toOrderProducts() {

		return orderItems.stream().map(OrderItem::toOrderProduct).collect(Collectors.toList());

	}

	record OrderItem(long quantity, long productId) {

		public OrderProduct toOrderProduct() {
			return new OrderProduct(this.quantity, this.productId);
		}
	}

}
