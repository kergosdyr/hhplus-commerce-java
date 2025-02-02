package kr.hhplus.be.server.api.response;

import java.util.List;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductSellerOutput;

public record TopSellerResponse(int periodDays, List<TopSellerInfo> topSellers) {

	public static TopSellerResponse fromEntities(int periodDays, List<ProductSellerOutput> orderDetailProducts) {

		return new TopSellerResponse(periodDays,
			orderDetailProducts.stream().map(TopSellerInfo::fromEntity).toList());

	}

	record TopSellerInfo(long id, String name, long totalSold) {

		static TopSellerInfo fromEntity(ProductSellerOutput productSellerOutput) {

			Product product = productSellerOutput.product();
			long totalSold = productSellerOutput.sell();

			return new TopSellerInfo(product.getProductId(), product.getName(), totalSold);
		}


	}
}

