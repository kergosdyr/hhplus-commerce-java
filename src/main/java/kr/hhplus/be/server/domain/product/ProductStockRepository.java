package kr.hhplus.be.server.domain.product;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public interface ProductStockRepository {

	Optional<ProductStock> findByProductId(long productId);

	ProductStock save(ProductStock stock);
}
