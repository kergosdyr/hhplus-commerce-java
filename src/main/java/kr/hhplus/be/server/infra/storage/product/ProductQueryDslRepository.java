package kr.hhplus.be.server.infra.storage.product;

import java.util.List;

import kr.hhplus.be.server.domain.product.ProductSellerOutput;

public interface ProductQueryDslRepository {

	List<ProductSellerOutput> findAllTopSellers(int days);
}
