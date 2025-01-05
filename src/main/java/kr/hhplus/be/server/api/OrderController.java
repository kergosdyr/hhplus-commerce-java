package kr.hhplus.be.server.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.api.config.ApiResponse;
import kr.hhplus.be.server.api.request.OrderRequest;
import kr.hhplus.be.server.api.response.OrderResponse;
import kr.hhplus.be.server.domain.order.OrderPayment;
import kr.hhplus.be.server.domain.order.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {

		OrderPayment orderPayment = orderService.order(request.userId(), request.toOrderProducts());

		return ApiResponse.success(OrderResponse.fromEntity(orderPayment));
	}

}
