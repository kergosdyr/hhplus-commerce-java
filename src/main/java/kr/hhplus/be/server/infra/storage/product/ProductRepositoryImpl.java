package kr.hhplus.be.server.infra.storage.product;

import static kr.hhplus.be.server.domain.order.QOrder.order;
import static kr.hhplus.be.server.domain.order.QOrderDetail.orderDetail;
import static kr.hhplus.be.server.domain.product.QProduct.product;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository, ProductQueryDslRepository {

	private final ProductJpaRepository productJpaRepository;

	private final JPAQueryFactory queryFactory;



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
	public List<Product> findAllTopSellers(int days) {

		LocalDateTime startDate = LocalDateTime.now().minusDays(days);

		return queryFactory
			.select(product)
			.from(order)
			.join(order.orderDetails, orderDetail)
			.join(orderDetail.product, product)
			.where(order.createdAt.goe(startDate))
			.groupBy(product.productId)
			.orderBy(orderDetail.quantity.sum().desc())
			.limit(5)
			.fetch();

	}
}
