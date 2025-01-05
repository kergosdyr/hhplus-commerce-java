package kr.hhplus.be.server.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.api.config.ApiResponse;
import kr.hhplus.be.server.api.config.PageInfo;
import kr.hhplus.be.server.api.response.ProductListResponse;
import kr.hhplus.be.server.api.response.TopSellerResponse;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping
	public ApiResponse<ProductListResponse> getProducts(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String keyword
	) {

		List<Product> products = productService.find(keyword, page, size);
		long count = productService.findCount(keyword);

		return ApiResponse.success(ProductListResponse.fromEntities(products, new PageInfo(page, size, count)));
	}

	@GetMapping("/top-sellers")
	public ApiResponse<TopSellerResponse> getTopSellers(
		@RequestParam(defaultValue = "3") int days) {
		return ApiResponse.success(TopSellerResponse.mock(days));
	}

}
