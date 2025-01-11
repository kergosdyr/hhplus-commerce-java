package kr.hhplus.be.server.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.infra.storage.product.ProductStockJpaRepository;

class ProductStockModifierIntegrationTest extends IntegrationTest {

	@Autowired
	private ProductStockModifier productStockModifier;

	@Autowired
	private ProductStockJpaRepository productStockRepository;

	@Test
	@DisplayName("정상 케이스: DB에 저장된 재고가 감소")
	void sell_decreaseStockInDb() {
		var stock1 = ProductStock.builder()
			.productId(1L)
			.stock(10L)
			.build();
		var stock2 = ProductStock.builder()
			.productId(2L)
			.stock(5L)
			.build();

		ProductStock savedStock1 = productStockRepository.save(stock1);
		ProductStock savedStock2 = productStockRepository.save(stock2);

		var orderProducts = List.of(
			new OrderProduct(1L, 1L),
			new OrderProduct(2L, 2L)
		);

		productStockModifier.sell(orderProducts);

		var updated1 = productStockRepository.findById(savedStock1.getStockId()).orElseThrow();
		var updated2 = productStockRepository.findById(savedStock2.getStockId()).orElseThrow();

		assertThat(updated1.getStock()).isEqualTo(9L);
		assertThat(updated2.getStock()).isEqualTo(3L);
	}

	@Test
	@DisplayName("재고 정보가 없는 경우 -> PRODUCT_NOT_FOUND 예외")
	void sell_throwNotFoundInDb() {
		var orderProducts = List.of(new OrderProduct(1L, 999L));

		assertThatThrownBy(() -> productStockModifier.sell(orderProducts))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("해당하는 상품이 존재하지 않습니다");
	}

	@Test
	@DisplayName("재고가 0인 경우 -> PRODUCT_NO_STOCK 예외")
	void sell_throwNoStockInDb() {
		var noStock = ProductStock.builder()
			.productId(3L)
			.stock(0L)
			.build();

		productStockRepository.save(noStock);

		var orderProducts = List.of(new OrderProduct(2L, 3L));

		assertThatThrownBy(() -> productStockModifier.sell(orderProducts))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("해당 상품은 품절되었습니다");
	}
}
