package kr.hhplus.be.server.api.response;

import java.util.List;
import java.util.stream.Collectors;

import kr.hhplus.be.server.api.config.PageInfo;
import kr.hhplus.be.server.domain.product.Product;

public record ProductListResponse(List<ProductInfo> items, PageInfo pageInfo) {

	public static ProductListResponse fromEntities(List<Product> products, PageInfo pageInfo) {
		return new ProductListResponse(products.stream().map(ProductInfo::fromEntity).collect(Collectors.toList()),
			pageInfo);
	}

	record ProductInfo(long id, String name, long price, long stock) {

		public static ProductInfo fromEntity(Product product) {
			return new ProductInfo(product.getProductId(), product.getName(), product.getPrice(),
				product.getProductStock().getStock());
		}

	}

}

