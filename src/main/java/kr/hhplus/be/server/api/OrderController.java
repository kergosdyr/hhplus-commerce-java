package kr.hhplus.be.server.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.api.config.WebApiResponse;
import kr.hhplus.be.server.api.request.OrderRequest;
import kr.hhplus.be.server.api.response.OrderResponse;
import kr.hhplus.be.server.domain.order.OrderPayment;
import kr.hhplus.be.server.domain.order.OrderService;
import lombok.RequiredArgsConstructor;

@Tag(name = "Order", description = "주문/결제 API")
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

	private final OrderService orderService;

	@Operation(
		summary = "주문 생성",
		description = "여러 상품을 동시에 주문하고, 결제(잔액 차감/쿠폰 사용)를 진행합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "주문/결제 성공"),
		@ApiResponse(responseCode = "400", description = "요청 데이터 오류 or 재고 부족 or 잔액 부족"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping
	public WebApiResponse<OrderResponse> createOrder(
		@Parameter(
			description = "주문 생성 요청 바디", required = true
		)
		@Valid @RequestBody OrderRequest request
	) {
		OrderPayment orderPayment = orderService.order(request.userId(), request.toOrderProducts());
		return WebApiResponse.success(OrderResponse.fromEntity(orderPayment));
	}
}
