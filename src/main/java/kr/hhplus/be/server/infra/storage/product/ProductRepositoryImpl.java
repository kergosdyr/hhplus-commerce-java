package kr.hhplus.be.server.infra.storage.product;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductSell;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

	private final ProductJpaRepository productJpaRepository;

	@Override
	public List<Product> findAll(int page, int size) {
		return productJpaRepository.findAllWithoutCount(Pageable.ofSize(size).withPage(page));
	}

	@Override
	public List<Product> findAllByKeyword(String keyword, int page, int size) {
		return productJpaRepository.findAllByKeywordWithoutCount(keyword, Pageable.ofSize(size).withPage(page));
	}

	@Override
	public long countAll() {
		return productJpaRepository.count();
	}

	@Override
	public long countAllByKeyword(String keyword) {
		return productJpaRepository.countAllByKeyword(keyword);
	}

	@Override
	public List<ProductSell> findAllTopSellers(int days) {
		return productJpaRepository.findAllTopSellers(days);
	}

}
