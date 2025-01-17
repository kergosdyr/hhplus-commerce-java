package kr.hhplus.be.server.api.response;

import java.time.LocalDateTime;
import java.util.List;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDetail;
import kr.hhplus.be.server.domain.order.OrderPayment;
import kr.hhplus.be.server.domain.payment.Payment;

public record OrderResponse(long orderId,
							long userId,
							String status,
							long totalAmount,
							long discountAmount,
							long paymentId,
							String paymentStatus,
							List<OrderItem> orderItems,
							LocalDateTime createdAt) {

	public static OrderResponse fromEntity(OrderPayment orderPayment) {

		Order order = orderPayment.order();
		Payment payment = orderPayment.payment();
		return new OrderResponse(
			order.getOrderId(),
			order.getUserId(),
			order.getStatus().name(),
			order.getTotalAmount(),
			order.getTotalAmount() - payment.getPaymentAmount(),
			payment.getPaymentId(),
			payment.getStatus().name(),
			order.getOrderDetails().stream().map(OrderItem::from).toList(),
			order.getCreatedAt()
		);

	}

	record OrderItem(long productId, long quantity, long price) {
		public static OrderItem from(OrderDetail orderDetail) {
			return new OrderItem(orderDetail.getProductId(), orderDetail.getQuantity(), orderDetail.getProductPrice());
		}
	}

}
