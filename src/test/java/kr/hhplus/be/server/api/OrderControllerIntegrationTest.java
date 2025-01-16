package kr.hhplus.be.server.api;

import static kr.hhplus.be.server.config.TestUtil.createTestBalance;
import static kr.hhplus.be.server.config.TestUtil.createTestCoupon;
import static kr.hhplus.be.server.config.TestUtil.createTestCouponInventory;
import static kr.hhplus.be.server.config.TestUtil.createTestUser;
import static kr.hhplus.be.server.config.TestUtil.createTestUserCoupon;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import kr.hhplus.be.server.api.request.OrderRequest;
import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.enums.OrderStatus;
import kr.hhplus.be.server.enums.PaymentStatus;

class OrderControllerIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("[POST] /api/v1/order - 주문/결제 성공 (쿠폰 미사용) 테스트")
	void createOrderSuccessWithCoupon() throws Exception {

		//given
		User user = userJpaRepository.save(createTestUser());
		balanceJpaRepository.save(createTestBalance(user.getUserId(), 50000L));
		Product product1 = productJpaRepository.save(TestUtil.createTestProduct("상품1", 10000L));
		Product product2 = productJpaRepository.save(TestUtil.createTestProduct("상품2", 10000L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product1.getProductId(), 10L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product2.getProductId(), 10L));

		var orderItems = List.of(
			new OrderRequest.OrderItem(2L, product1.getProductId()),
			new OrderRequest.OrderItem(3L, product2.getProductId())
		);

		// request: userId, couponId, orderItems
		var request = new OrderRequest(user.getUserId(), null, orderItems);

		// when
		var response = RestAssured
			.given()
			.contentType("application/json")
			.body(objectMapper.writeValueAsString(request))
			.when()
			.post("/api/v1/order")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.extract()
			.jsonPath();

		// then
		assertThat(response.getLong("data.orderId")).isPositive();
		assertThat(response.getLong("data.userId")).isEqualTo(user.getUserId());
		assertThat(response.getString("data.status")).isEqualTo(OrderStatus.PAID.name());
		assertThat(response.getLong("data.totalAmount")).isGreaterThan(2 * 500 + 3 * 300);
		assertThat(response.getString("data.paymentStatus")).isEqualTo(PaymentStatus.PAID.name());
		assertThat(response.getList("data.orderItems")).hasSize(2);

		long orderId = response.getLong("data.orderId");
		Order savedOrder = orderJpaRepository.findById(orderId).orElseThrow();
		Payment savedPayment = paymentJpaRepository.findAllByOrderId(orderId).get(0);

		assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PAID);
		assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.PAID);
	}

	@Test
	@DisplayName("[POST] /api/v1/order - 주문/결제 성공 (쿠폰 사용) 테스트")
	void createOrderSuccessWithoutCoupon() throws Exception {
		// given
		User user = userJpaRepository.save(createTestUser());
		Coupon savedCoupon = couponJpaRepository.save(createTestCoupon(LocalDateTime.now().plusDays(1L)));
		UserCoupon userCoupon = userCouponJpaRepository.save(
			createTestUserCoupon(user.getUserId(), savedCoupon.getCouponId()));
		couponInventoryJpaRepository.save(createTestCouponInventory(savedCoupon.getCouponId(), 30L));
		balanceJpaRepository.save(createTestBalance(user.getUserId(), 50000L));
		Product product1 = productJpaRepository.save(TestUtil.createTestProduct("상품1", 10000L));
		Product product2 = productJpaRepository.save(TestUtil.createTestProduct("상품2", 10000L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product1.getProductId(), 10L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product2.getProductId(), 10L));

		var orderItems = List.of(
			new OrderRequest.OrderItem(2L, product1.getProductId()),
			new OrderRequest.OrderItem(3L, product2.getProductId())
		);

		var request = new OrderRequest(user.getUserId(), savedCoupon.getCouponId(), orderItems);

		// when
		var response = RestAssured
			.given()
			.contentType("application/json")
			.body(objectMapper.writeValueAsString(request))
			.when()
			.post("/api/v1/order")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.extract()
			.jsonPath();

		// then
		assertThat(response.getLong("data.orderId")).isPositive();
		assertThat(response.getLong("data.userId")).isEqualTo(user.getUserId());
		assertThat(response.getString("data.status")).isEqualTo(OrderStatus.PAID.name());
		assertThat(response.getString("data.paymentStatus")).isEqualTo(PaymentStatus.PAID.name());
	}

	@Test
	@DisplayName("[POST] /api/v1/order - userId가 잘못된 경우 Validation 실패 테스트")
	void createOrderWithInvalidUserId() throws Exception {
		// given
		var orderItems = List.of(new OrderRequest.OrderItem(2L, 1L));
		var request = new OrderRequest(0L, 999L, orderItems); // userId=0

		// when
		var response = RestAssured
			.given()
			.contentType("application/json")
			.body(objectMapper.writeValueAsString(request))
			.when()
			.post("/api/v1/order")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.extract()
			.jsonPath();

		// then
		// 에러 응답 필드(예: "error.code", "error.message", "error.data" 등) 검증
		assertThat(response.getString("error.code")).isEqualTo("E400");
		assertThat(response.getString("error.message")).contains("잘못된 요청 정보를 전송하셨습니다");
		assertThat(response.getString("error.data")).contains("userId는 1 이상의 값");
	}

	@Test
	@DisplayName("[POST] /api/v1/order - OrderItem의 productId가 0인 경우 Validation 실패 테스트")
	void createOrderWithInvalidProductIdInOrderItem() throws Exception {
		// given
		var orderItems = List.of(new OrderRequest.OrderItem(2L, 0L)); // productId=0
		var request = new OrderRequest(123L, 999L, orderItems);

		// when
		var response = RestAssured
			.given()
			.contentType("application/json")
			.body(objectMapper.writeValueAsString(request))
			.when()
			.post("/api/v1/order")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.extract()
			.jsonPath();

		// then
		assertThat(response.getString("error.code")).isEqualTo("E400");
		assertThat(response.getString("error.message")).contains("잘못된 요청 정보를 전송하셨습니다");
		// 'productId는 1 이상의 값' 같은 메시지 포함 여부 확인
		assertThat(response.getString("error.data")).contains("productId는 1 이상의 값");
	}

	@Test
	@DisplayName("[POST] /api/v1/order - orderItems가 null인 경우 Validation 실패 테스트")
	void createOrderWithNullOrderItems() throws Exception {
		// given
		var request = new OrderRequest(123L, 999L, null); // orderItems=null

		// when
		var response = RestAssured
			.given()
			.contentType("application/json")
			.body(objectMapper.writeValueAsString(request))
			.when()
			.post("/api/v1/order")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.extract()
			.jsonPath();

		// then
		assertThat(response.getString("error.code")).isEqualTo("E400");
		assertThat(response.getString("error.message")).contains("잘못된 요청 정보를 전송하셨습니다");
		assertThat(response.getString("error.data")).contains("orderItems는 비어있으면 안 됩니다.");
	}
}
