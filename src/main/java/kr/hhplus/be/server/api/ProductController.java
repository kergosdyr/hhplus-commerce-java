package kr.hhplus.be.server.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.api.config.ApiResponse;
import kr.hhplus.be.server.api.response.ProductListResponse;
import kr.hhplus.be.server.api.response.TopSellerResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	@GetMapping
	public ApiResponse<ProductListResponse> getProducts(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String keyword
	) {
		return ApiResponse.success(ProductListResponse.mock(page));
	}

	@GetMapping("/top-sellers")
	public ApiResponse<TopSellerResponse> getTopSellers(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam(defaultValue = "3") int days) {
		return ApiResponse.success(TopSellerResponse.mock(days));
	}

}
