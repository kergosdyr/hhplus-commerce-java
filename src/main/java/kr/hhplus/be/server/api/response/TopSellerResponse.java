package kr.hhplus.be.server.api.response;

import java.util.List;

public record TopSellerResponse(int periodDays, List<TopSellerInfo> topSellers) {
	public static TopSellerResponse mock(int days) {
		return new TopSellerResponse(days, List.of(new TopSellerInfo(1L, "Apple iPad", 150),
			new TopSellerInfo(5L, "MacBook Air", 100), new TopSellerInfo(2L, "Galaxy Tab", 80)));
	}

	record TopSellerInfo(Long productId, String productName, int totalSoldQuantity) {
	}
}

