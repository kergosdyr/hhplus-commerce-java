package kr.hhplus.be.server.presentation.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.presentation.web.config.ApiResponse;
import kr.hhplus.be.server.presentation.web.request.OrderRequest;
import kr.hhplus.be.server.presentation.web.response.OrderResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

	@PostMapping
	public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
		return ApiResponse.success(OrderResponse.mock(request.userId()));
	}

}
