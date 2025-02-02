package kr.hhplus.be.server.infra.storage.product;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.ProductStockRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile({"lettuce", "redisson"})
@Primary
public class ProductStockRepositoryImpl implements ProductStockRepository {

	private final ProductStockJpaRepository productStockJpaRepository;

	@Override
	public Optional<ProductStock> findByProductId(long productId) {
		return productStockJpaRepository.findByProductId(productId);
	}

	@Override
	public ProductStock save(ProductStock stock) {
		return productStockJpaRepository.save(stock);
	}
}
