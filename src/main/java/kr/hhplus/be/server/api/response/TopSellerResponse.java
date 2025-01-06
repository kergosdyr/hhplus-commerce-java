package kr.hhplus.be.server.api.response;

import java.util.List;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductSell;

public record TopSellerResponse(int periodDays, List<TopSellerInfo> topSellers) {

	public static TopSellerResponse fromEntities(int periodDays, List<ProductSell> orderDetailProducts) {

		return new TopSellerResponse(periodDays,
			orderDetailProducts.stream().map(TopSellerInfo::fromEntity).toList());

	}

	record TopSellerInfo(long id, String name, long totalSold) {

		static TopSellerInfo fromEntity(ProductSell productSell) {

			Product product = productSell.product();
			long totalSold = productSell.sell();

			return new TopSellerInfo(product.getProductId(), product.getName(), totalSold);
		}


	}
}

