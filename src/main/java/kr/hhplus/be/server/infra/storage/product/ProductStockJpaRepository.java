package kr.hhplus.be.server.infra.storage.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.ProductStock;

public interface ProductStockJpaRepository extends JpaRepository<ProductStock, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from ProductStock p where p.productId = ?1")
	Optional<ProductStock> findByProductIdWithLock(long productId);

	Optional<ProductStock> findByProductId(long productId);
}
