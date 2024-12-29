package kr.hhplus.be.server.presentation.web.response;

import java.util.List;

public record OrderResponse(Long orderId,
							Long userId,
							String orderStatus,
							int totalAmount,
							int discountApplied,
							int paidAmount,
							Long paymentId,
							String paymentStatus,
							List<OrderItemDto> orderItems,
							String createdAt) {

	public static OrderResponse mock(long userId) {

		return new OrderResponse(
			20240001L,
			userId,
			"PAID",
			150000,
			15000,
			135000,
			50001L,
			"SUCCESS",
			List.of(new OrderItemDto(1L, 2, 50000), new OrderItemDto(2L, 3, 20000)),
			"2024-01-01T10:00:00"
		);

	}

	record OrderItemDto(Long productId, int quantity, int itemPrice) {
	}

}
