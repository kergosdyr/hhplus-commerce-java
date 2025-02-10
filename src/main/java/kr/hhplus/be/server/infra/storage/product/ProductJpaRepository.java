package kr.hhplus.be.server.infra.storage.product;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.hhplus.be.server.domain.product.Product;

public interface ProductJpaRepository extends JpaRepository<Product, Long>, ProductQueryDslRepository {

	@EntityGraph(attributePaths = {"productStock"})
	@Query("select p from Product p")
	List<Product> findAllWithoutCount(Pageable pageable);

	@EntityGraph(attributePaths = {"productStock"})
	@Query("select p from Product p where upper(p.name) like upper(concat('%', ?1, '%'))")
	List<Product> findAllByKeywordWithoutCount(String name, Pageable pageable);

	@Query("select count(p) from Product p where upper(p.name) like upper(concat('%', ?1, '%'))")
	long countAllByKeyword(String name);

}
