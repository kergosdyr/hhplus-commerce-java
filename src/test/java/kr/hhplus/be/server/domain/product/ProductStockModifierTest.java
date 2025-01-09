package kr.hhplus.be.server.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.error.ApiException;

@ExtendWith(MockitoExtension.class)
class ProductStockModifierTest {

	@Mock
	private ProductStockRepository productStockRepository;

	@InjectMocks
	private ProductStockModifier productStockModifier;

	@Test
	@DisplayName("정상 케이스: 재고가 있고(isSellable() == true) -> sell() 호출로 재고 감소")
	void sell_decreaseStock() {
		var orderProducts = List.of(
			new OrderProduct(2L, 1L),
			new OrderProduct(1L, 2L)
		);

		var stock1 = ProductStock.builder()
			.stockId(1L)
			.productId(1L)
			.stock(10L)
			.build();

		var stock2 = ProductStock.builder()
			.stockId(2L)
			.productId(2L)
			.stock(5L)
			.build();

		given(productStockRepository.findByProductId(1L))
			.willReturn(Optional.of(stock1));
		given(productStockRepository.findByProductId(2L))
			.willReturn(Optional.of(stock2));

		var result = productStockModifier.sell(orderProducts);

		assertThat(result).hasSize(2);
		assertThat(stock1.getStock()).isEqualTo(8L);
		assertThat(stock2.getStock()).isEqualTo(4L);
	}

	@Test
	@DisplayName("재고 정보가 없는 경우 -> PRODUCT_NOT_FOUND 예외")
	void sell_throwWhenNotFound() {
		var orderProducts = List.of(new OrderProduct(1L, 999L));
		given(productStockRepository.findByProductId(999L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> productStockModifier.sell(orderProducts))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("해당하는 상품이 존재하지 않습니다");
	}

	@Test
	@DisplayName("재고가 0이어서 isSellable() == false -> PRODUCT_NO_STOCK 예외")
	void sell_throwWhenNoStock() {
		var orderProducts = List.of(new OrderProduct(2L, 1L));
		var stock = ProductStock.builder()
			.stockId(1L)
			.productId(1L)
			.stock(0L)
			.build();

		given(productStockRepository.findByProductId(1L))
			.willReturn(Optional.of(stock));

		assertThatThrownBy(() -> productStockModifier.sell(orderProducts))
			.isInstanceOf(ApiException.class)
			.hasMessageContaining("해당 상품은 품절되었습니다");
	}
}
