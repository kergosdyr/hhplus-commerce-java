package kr.hhplus.be.server.api.response;

import java.util.List;

import kr.hhplus.be.server.api.config.PageInfo;

public record ProductListResponse(List<ProductInfo> items, PageInfo pageInfo) {

	public static ProductListResponse mock(int page) {
		return new ProductListResponse(
			List.of(new ProductInfo(1L, "Apple iPad", 500000, 100),
				new ProductInfo(2L, "Samsung Galaxy Tab", 400000, 50)),
			new PageInfo(page, 5, 10)
		);
	}

	record ProductInfo(long id, String name, int price, int stock) {
	}

}

