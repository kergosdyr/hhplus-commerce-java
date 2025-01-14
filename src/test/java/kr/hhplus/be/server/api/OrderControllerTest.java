package kr.hhplus.be.server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import kr.hhplus.be.server.api.request.OrderRequest;
import kr.hhplus.be.server.config.WebIntegrationTest;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDetail;
import kr.hhplus.be.server.domain.order.OrderPayment;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.enums.OrderStatus;
import kr.hhplus.be.server.enums.PaymentStatus;

class OrderControllerTest extends WebIntegrationTest {

	@Test
	@DisplayName("[POST] /api/v1/order - 주문 생성/결제 성공 테스트")
	void createOrderSuccessTest() throws Exception {
		// given
		var orderItems = List.of(
			new OrderRequest.OrderItem(2L, 1L), // Product ID: 1, Quantity: 2
			new OrderRequest.OrderItem(3L, 2L)  // Product ID: 2, Quantity: 3
		);

		var request = new OrderRequest(123L, 456L, orderItems); // userId, couponId, orderItems

		var mockOrderDetails = List.of(
			OrderDetail.builder()
				.productId(1L)
				.quantity(2L)
				.product(Product.builder()
					.productId(1L)
					.price(500L)
					.build())
				.build(),
			OrderDetail.builder()
				.productId(2L)
				.quantity(3L)
				.product(Product.builder()
					.productId(2L)
					.price(300L)
					.build())
				.build()
		);

		var mockOrder = Order.builder()
			.orderId(1L)
			.userId(123L)
			.total(2000L)
			.status(OrderStatus.PAID)
			.orderDetails(mockOrderDetails)
			.build();

		var mockPayment = Payment.builder()
			.paymentId(1L)
			.orderId(1L)
			.userId(123L)
			.isUsedCoupon(true)
			.totalPrice(1800L)
			.couponId(456L)
			.couponAppliedPrice(200L)
			.status(PaymentStatus.PAID)
			.build();

		var mockOrderPayment = new OrderPayment(mockOrder, mockPayment);

		Mockito.when(orderService.orderWithCoupon(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList()))
			.thenReturn(mockOrderPayment);

		// when & then
		mockMvc.perform(
				post("/api/v1/order")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.orderId").value(1L))
			.andExpect(jsonPath("$.data.userId").value(123L))
			.andExpect(jsonPath("$.data.status").value("PAID"))
			.andExpect(jsonPath("$.data.totalAmount").value(2000L))
			.andExpect(jsonPath("$.data.discountAmount").value(100L))
			.andExpect(jsonPath("$.data.paidAmount").value(1800L))
			.andExpect(jsonPath("$.data.paymentId").value(1L))
			.andExpect(jsonPath("$.data.paymentStatus").value("PAID"))
			.andExpect(jsonPath("$.data.orderItems.length()").value(2))
			.andExpect(jsonPath("$.data.orderItems[0].productId").value(1L))
			.andExpect(jsonPath("$.data.orderItems[0].quantity").value(2L))
			.andExpect(jsonPath("$.data.orderItems[0].price").value(500L))
			.andExpect(jsonPath("$.data.orderItems[1].productId").value(2L))
			.andExpect(jsonPath("$.data.orderItems[1].quantity").value(3L))
			.andExpect(jsonPath("$.data.orderItems[1].price").value(300L));
	}

	@Test
	@DisplayName("[POST] /api/v1/order - 주문 생성/결제 성공 테스트(쿠폰 없음)")
	void createOrderSuccessTestWithoutCoupon() throws Exception {
		// given
		var orderItems = List.of(
			new OrderRequest.OrderItem(2L, 1L), // Product ID: 1, Quantity: 2
			new OrderRequest.OrderItem(3L, 2L)  // Product ID: 2, Quantity: 3
		);

		var request = new OrderRequest(123L, null, orderItems); // userId, couponId, orderItems

		var mockOrderDetails = List.of(
			OrderDetail.builder()
				.productId(1L)
				.quantity(2L)
				.product(Product.builder()
					.productId(1L)
					.price(500L)
					.build())
				.build(),
			OrderDetail.builder()
				.productId(2L)
				.quantity(3L)
				.product(Product.builder()
					.productId(2L)
					.price(300L)
					.build())
				.build()
		);

		var mockOrder = Order.builder()
			.orderId(1L)
			.userId(123L)
			.total(2000L)
			.status(OrderStatus.PAID)
			.orderDetails(mockOrderDetails)
			.build();

		var mockPayment = Payment.builder()
			.paymentId(1L)
			.orderId(1L)
			.userId(123L)
			.isUsedCoupon(true)
			.totalPrice(1800L)
			.couponId(456L)
			.couponAppliedPrice(200L)
			.status(PaymentStatus.PAID)
			.build();

		var mockOrderPayment = new OrderPayment(mockOrder, mockPayment);

		Mockito.when(orderService.order(Mockito.anyLong(), Mockito.anyList()))
			.thenReturn(mockOrderPayment);

		// when & then
		mockMvc.perform(
				post("/api/v1/order")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.orderId").value(1L))
			.andExpect(jsonPath("$.data.userId").value(123L))
			.andExpect(jsonPath("$.data.status").value("PAID"))
			.andExpect(jsonPath("$.data.totalAmount").value(2000L))
			.andExpect(jsonPath("$.data.discountAmount").value(100L))
			.andExpect(jsonPath("$.data.paidAmount").value(1800L))
			.andExpect(jsonPath("$.data.paymentId").value(1L))
			.andExpect(jsonPath("$.data.paymentStatus").value("PAID"))
			.andExpect(jsonPath("$.data.orderItems.length()").value(2))
			.andExpect(jsonPath("$.data.orderItems[0].productId").value(1L))
			.andExpect(jsonPath("$.data.orderItems[0].quantity").value(2L))
			.andExpect(jsonPath("$.data.orderItems[0].price").value(500L))
			.andExpect(jsonPath("$.data.orderItems[1].productId").value(2L))
			.andExpect(jsonPath("$.data.orderItems[1].quantity").value(3L))
			.andExpect(jsonPath("$.data.orderItems[1].price").value(300L));
	}

	@Test
	@DisplayName("[POST] /api/v1/order - userId가 0인 경우 Validation 실패")
	void createOrderWithInvalidUserId() throws Exception {
		// given
		// userId=0 => @Min(1) 위배
		var request = new OrderRequest(0L, 999L, List.of(new OrderRequest.OrderItem(1L, 2L)));

		// when & then
		mockMvc.perform(
				post("/api/v1/order")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.code").value("E400"))
			.andExpect(jsonPath("$.error.message").value(org.hamcrest.Matchers.containsString("잘못된 요청 정보를 전송하셨습니다")))
			.andExpect(jsonPath("$.error.data").value(org.hamcrest.Matchers.containsString("userId는 1 이상의 값")));
	}

	@Test
	@DisplayName("[POST] /api/v1/order - OrderItem의 quantity=0 Validation 실패")
	void createOrderWithInvalidQuantity() throws Exception {
		// given
		var request = new OrderRequest(
			123L,
			999L,
			List.of(new OrderRequest.OrderItem(0L, 2L))  // invalid
		);

		// Mocking (실제 로직이 호출되지 않도록)
		Mockito.when(orderService.order(Mockito.anyLong(), Mockito.anyList())).thenReturn(new OrderPayment(
			null, null
		));

		// when & then
		mockMvc.perform(
				post("/api/v1/order")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.code").value("E400"))
			.andExpect(jsonPath("$.error.message").value(org.hamcrest.Matchers.containsString("잘못된 요청 정보를 전송하셨습니다")))
			.andExpect(jsonPath("$.error.data").value(org.hamcrest.Matchers.containsString("quantity는 1 이상의 값")));
	}

	@Test
	@DisplayName("[POST] /api/v1/order - orderItems=null Validation 실패")
	void createOrderWithNullOrderItems() throws Exception {
		// given
		// orderItems=null => @NotNull(message="orderItems는 비어있으면 안 됩니다.")
		var request = new OrderRequest(123L, 999L, null);

		mockMvc.perform(
				post("/api/v1/order")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.code").value("E400"))
			.andExpect(jsonPath("$.error.message").value(org.hamcrest.Matchers.containsString("잘못된 요청 정보를 전송하셨습니다")))
			.andExpect(
				jsonPath("$.error.data").value(org.hamcrest.Matchers.containsString("orderItems는 비어있으면 안 됩니다.")));
	}

}
