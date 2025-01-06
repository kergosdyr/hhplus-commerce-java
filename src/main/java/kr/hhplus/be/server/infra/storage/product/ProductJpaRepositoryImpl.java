package kr.hhplus.be.server.infra.storage.product;

import static kr.hhplus.be.server.domain.order.QOrder.order;
import static kr.hhplus.be.server.domain.order.QOrderDetail.orderDetail;
import static kr.hhplus.be.server.domain.product.QProduct.product;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.hhplus.be.server.domain.product.ProductSell;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductJpaRepositoryImpl implements ProductQueryDslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ProductSell> findAllTopSellers(int days) {

		LocalDateTime startDate = LocalDateTime.now().minusDays(days);

		return queryFactory
			.select(Projections.constructor(
				ProductSell.class,
				product,
				orderDetail.quantity.sum()
			))
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
