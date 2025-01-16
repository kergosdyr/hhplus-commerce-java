package kr.hhplus.be.server.domain.product;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public interface ProductRepository {
	List<Product> findAll(int page, int size);

	List<Product> findAllByKeyword(String keyword, int page, int size);

	long countAll();

	long countAllByKeyword(String keyword);

	List<ProductSell> findAllTopSellers(int days);

	Product findById(long productId);
}
