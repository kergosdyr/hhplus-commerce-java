package kr.hhplus.be.server.infra.storage.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.product.ProductStock;

public interface ProductStockJpaRepository extends JpaRepository<ProductStock, Long> {


	Optional<ProductStock> findByProductId(long productId);
}
