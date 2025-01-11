package kr.hhplus.be.server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import kr.hhplus.be.server.api.config.PageInfo;
import kr.hhplus.be.server.config.WebIntegrationTest;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductSell;
import kr.hhplus.be.server.domain.product.ProductStock;

class ProductControllerTest extends WebIntegrationTest {
	@Test
	@DisplayName("[GET] /api/v1/products - 상품 목록 조회 성공 테스트")
	void getProductsSuccessTest() throws Exception {
		// given
		var mockProducts = List.of(
			Product.builder()
				.productId(1L)
				.name("Apple iPad")
				.price(500000L)
				.status("AVAILABLE")
				.productStock(ProductStock.builder()
					.stock(100L)
					.build())
				.build(),
			Product.builder()
				.productId(2L)
				.name("Galaxy Tab")
				.price(400000L)
				.status("AVAILABLE")
				.productStock(ProductStock.builder()
					.stock(50L)
					.build())
				.build()
		);

		var mockPageInfo = new PageInfo(1, 10, 2);

		Mockito.when(productService.find(null, 1, 10)).thenReturn(mockProducts);
		Mockito.when(productService.findCount(null)).thenReturn(2L);

		// when & then
		mockMvc.perform(get("/api/v1/products?page=1&size=10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.items.length()").value(2))
			.andExpect(jsonPath("$.data.items[0].id").value(1L))
			.andExpect(jsonPath("$.data.items[0].name").value("Apple iPad"))
			.andExpect(jsonPath("$.data.items[0].price").value(500000L))
			.andExpect(jsonPath("$.data.items[0].stock").value(100L))
			.andExpect(jsonPath("$.data.items[1].id").value(2L))
			.andExpect(jsonPath("$.data.items[1].name").value("Galaxy Tab"))
			.andExpect(jsonPath("$.data.items[1].price").value(400000L))
			.andExpect(jsonPath("$.data.items[1].stock").value(50L))
			.andExpect(jsonPath("$.data.pageInfo.currentPage").value(1))
			.andExpect(jsonPath("$.data.pageInfo.size").value(10))
			.andExpect(jsonPath("$.data.pageInfo.totalItems").value(2));
	}

	@Test
	@DisplayName("[GET] /api/v1/products/top-sellers - 상위 판매 상품 조회 성공 테스트")
	void getTopSellersSuccessTest() throws Exception {
		// given
		var mockTopSellers = List.of(
			new ProductSell(
				Product.builder()
					.productId(1L)
					.name("Apple iPad")
					.price(500000L)
					.status("AVAILABLE")
					.build(),
				150L
			),
			new ProductSell(
				Product.builder()
					.productId(5L)
					.name("MacBook Air")
					.price(1500000L)
					.status("AVAILABLE")
					.build(),
				100L
			)
		);

		Mockito.when(productService.findAllTopSellers(3)).thenReturn(mockTopSellers);

		// when & then
		mockMvc.perform(get("/api/v1/products/top-sellers?days=3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.topSellers.length()").value(2))
			.andExpect(jsonPath("$.data.periodDays").value(3));
	}

	@Test
	@DisplayName("[GET] /api/v1/products/top-sellers?days=0 - Validation 실패")
	void getTopSellersWithInvalidDays() throws Exception {
		// given

		Mockito.when(productService.findAllTopSellers(0)).thenReturn(List.of());

		// when & then
		mockMvc.perform(get("/api/v1/products/top-sellers?days=0"))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.code").value("E400"))
			.andExpect(jsonPath("$.error.message").value(org.hamcrest.Matchers.containsString("잘못된 요청 정보를 전송하셨습니다")))
			.andExpect(jsonPath("$.error.data").value(org.hamcrest.Matchers.containsString("days는 3 이상의 값")));
	}

}
