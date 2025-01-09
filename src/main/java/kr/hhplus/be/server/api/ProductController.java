package kr.hhplus.be.server.api;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import kr.hhplus.be.server.api.config.PageInfo;
import kr.hhplus.be.server.api.config.WebApiResponse;
import kr.hhplus.be.server.api.response.ProductListResponse;
import kr.hhplus.be.server.api.response.TopSellerResponse;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductSell;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product", description = "상품 조회 API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

	private final ProductService productService;

	@Operation(
		summary = "상품 목록 조회",
		description = "판매 중인 상품 목록을 페이징 및 키워드 검색으로 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "검색 파라미터 오류"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@GetMapping
	public WebApiResponse<ProductListResponse> getProducts(
		@Parameter(
			name = "page", description = "페이지 번호", in = ParameterIn.QUERY, required = false
		)
		@Min(value = 1, message = "page는 1 이상의 값이어야 합니다.") @RequestParam(defaultValue = "1") int page,

		@Parameter(
			name = "size", description = "페이지 크기", in = ParameterIn.QUERY, required = false
		)
		@Min(value = 1, message = "size는 1 이상의 값이어야 합니다.") @RequestParam(defaultValue = "10") int size,

		@Parameter(
			name = "keyword", description = "상품명 검색어", in = ParameterIn.QUERY, required = false
		)
		@RequestParam(required = false) String keyword
	) {
		List<Product> products = productService.find(keyword, page, size);
		long count = productService.findCount(keyword);

		return WebApiResponse.success(
			ProductListResponse.fromEntities(products, new PageInfo(page, size, count))
		);
	}

	@Operation(
		summary = "상위 판매 상품 조회",
		description = "최근 N일 간 가장 많이 팔린 상위 상품들을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "조회 파라미터 오류"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@GetMapping("/top-sellers")
	public WebApiResponse<TopSellerResponse> getTopSellers(
		@Parameter(
			name = "days", description = "며칠간 판매량 기준 (기본값 3)", in = ParameterIn.QUERY, required = false
		)
		@Min(value = 3, message = "days는 3 이상의 값이어야 합니다.") @RequestParam(defaultValue = "3") int days
	) {
		List<ProductSell> allTopSellers = productService.findAllTopSellers(days);
		return WebApiResponse.success(TopSellerResponse.fromEntities(days, allTopSellers));
	}
}
