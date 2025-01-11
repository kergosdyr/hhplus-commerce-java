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

	//TODO 무분별한 N+1 등 조회 성능 문제가 발생할 수 있으므로, 추후 로직 변경 반드시 필요함, 현재는 요구사항은 성능 문제가 없으므로 제외.
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
