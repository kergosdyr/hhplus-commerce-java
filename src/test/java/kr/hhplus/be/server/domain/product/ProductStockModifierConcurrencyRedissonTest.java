package kr.hhplus.be.server.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import kr.hhplus.be.server.config.ConcurrencyTestUtil;
import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.domain.order.OrderCommand;

@TestPropertySource(properties = {"spring.profiles.active=test,redisson"})
class ProductStockModifierConcurrencyRedissonTest extends IntegrationTest {

	@Test
	@DisplayName("10개의 재고와 5개의 재고가 있는 상품에 대해 동시에 재고를 1, 2를 차감하는 경우 5회만 성공하고 5회 실패해야한다.")
	void shouldFail5Success5WhenConcurrent10() throws InterruptedException {
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
			new OrderCommand(1L, 2L),
			new OrderCommand(2L, 1L)
		);

		var result = ConcurrencyTestUtil.run(10, () -> {
			try {
				productStockModifier.sell(orderProducts);
				return true;
			} catch (Exception e) {
				return false;
			}
		});

		Assertions.assertThat(result.success()).isEqualTo(5);
		Assertions.assertThat(result.fail()).isEqualTo(5);

		var updated1 = productStockRepository.findById(savedStock1.getStockId()).orElseThrow();
		var updated2 = productStockRepository.findById(savedStock2.getStockId()).orElseThrow();

		assertThat(updated1.getStock()).isEqualTo(0L);
		assertThat(updated2.getStock()).isEqualTo(0L);

	}

}
