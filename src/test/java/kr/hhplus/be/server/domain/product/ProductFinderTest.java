package kr.hhplus.be.server.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductFinderTest {

	@Mock
	ProductRepository productRepository;

	@InjectMocks
	ProductFinder productFinder;

	private static @NotNull List<Product> testProducts() {
		return List.of(
			Product.builder()
				.productId(1L)
				.name("상품1")
				.price(1000L)
				.build(),
			Product.builder()
				.productId(2L)
				.name("상품2")
				.price(2000L)
				.build()
		);
	}

	@Test
	@DisplayName("키워드가 없는 경우에 조회 조건 없이 상품 목록만을 조회한다")
	void shouldFindProductsAllWhenNoKeyword() {

		// given
		List<Product> givenProducts = testProducts();
		given(productRepository.findAll(1, 10)).willReturn(givenProducts);

		//when
		List<Product> products = productFinder.find("", 1, 10);

		//then
		assertThat(products).isEqualTo(givenProducts);
		verify(productRepository, times(1)).findAll(1, 10);
		verify(productRepository, times(0)).findAllByKeyword("", 1, 10);
	}

	@Test
	@DisplayName("키워드가 있는 경우에 해당 키워드를 조회 조건으로 하여 상품 목록을 조회한다")
	void shouldFindProductsWithKeywordWhenNotBlankKeyword() {

		//given
		List<Product> givenProducts = testProducts();
		given(productRepository.findAllByKeyword("상품", 1, 10)).willReturn(givenProducts);

		//when
		List<Product> products = productFinder.find("상품", 1, 10);

		//then
		assertThat(products).isEqualTo(givenProducts);
		verify(productRepository, times(0)).findAll(1, 10);
		verify(productRepository, times(1)).findAllByKeyword("상품", 1, 10);

	}

	@Test
	@DisplayName("키워드가 없는 경우에 조회 조건 없이 모든 상품의 갯수를 조회한다")
	void shouldFindAllProductCountsWhenNoKeyword() {

		//given
		given(productRepository.countAll()).willReturn(2L);

		//when
		long count = productFinder.findCount("");

		//then
		assertThat(count).isEqualTo(2L);
		verify(productRepository, times(1)).countAll();
		verify(productRepository, times(0)).countAllByKeyword("");

	}

	@Test
	@DisplayName("키워드가 있는 경우에 조회 조건 포함하여 상품의 갯수를 조회한다.")
	void shouldFindCountAllProductsWithKeywordWhenNotBlankKeyword() {

		//given
		given(productRepository.countAllByKeyword("상품")).willReturn(2L);

		//when
		long count = productFinder.findCount("상품");

		//then
		assertThat(count).isEqualTo(2L);
		verify(productRepository, times(0)).countAll();
		verify(productRepository, times(1)).countAllByKeyword("상품");

	}

}