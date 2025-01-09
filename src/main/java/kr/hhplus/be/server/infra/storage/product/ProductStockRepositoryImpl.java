package kr.hhplus.be.server.infra.storage.product;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.ProductStockRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductStockRepositoryImpl implements ProductStockRepository {

	private final ProductStockJpaRepository productStockJpaRepository;

	@Override
	public Optional<ProductStock> findByProductId(long productId) {
		return productStockJpaRepository.findByProductIdWithLock(productId);
	}

	@Override
	public ProductStock save(ProductStock stock) {
		return productStockJpaRepository.save(stock);
	}
}
