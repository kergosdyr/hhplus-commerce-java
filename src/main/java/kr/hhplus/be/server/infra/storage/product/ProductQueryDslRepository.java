package kr.hhplus.be.server.infra.storage.product;

import java.util.List;

import kr.hhplus.be.server.domain.product.Product;

public interface ProductQueryDslRepository {

	List<Product> findAllTopSellers(int days);
}
