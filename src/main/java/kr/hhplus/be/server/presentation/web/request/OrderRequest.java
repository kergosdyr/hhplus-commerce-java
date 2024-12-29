package kr.hhplus.be.server.presentation.web.request;

import java.util.List;

public record OrderRequest(Long userId, List<OrderItem> orderItems, Long couponId) {
	record OrderItem(Long productId, int quantity) {
	}

}
