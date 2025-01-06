package kr.hhplus.be.server.infra.storage.product;

import java.util.List;

import kr.hhplus.be.server.domain.product.ProductSell;

public interface ProductQueryDslRepository {

	List<ProductSell> findAllTopSellers(int days);
}
