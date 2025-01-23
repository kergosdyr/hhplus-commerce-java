package kr.hhplus.be.server.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDetail;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.enums.OrderStatus;
import kr.hhplus.be.server.enums.ProductStatus;

public class ProductControllerIntegrationTest extends IntegrationTest {

	@BeforeEach
	void setUp() {

		// 테스트 데이터 삽입
		Product product1 = productJpaRepository.save(Product.builder()
			.name("Apple iPad")
			.price(500000L)
			.status(ProductStatus.AVAILABLE)
			.build());

		productStockJpaRepository.save(ProductStock.builder()
			.productId(product1.getProductId())
			.stock(10)
			.build());

		Product product2 = productJpaRepository.save(Product.builder()
			.name("Galaxy Tab")
			.price(400000L)
			.status(ProductStatus.AVAILABLE)
			.build());

		productStockJpaRepository.save(ProductStock.builder()
			.productId(product2.getProductId())
			.stock(10)
			.build());

		Product product3 = productJpaRepository.save(Product.builder()
			.name("MacBook Air")
			.price(1500000L)
			.status(ProductStatus.AVAILABLE)
			.build());

		productStockJpaRepository.save(ProductStock.builder()
			.productId(product3.getProductId())
			.stock(10)
			.build());

		// 상위 판매 상품을 위한 주문 및 주문 상세 삽입

		OrderDetail orderDetail1 = OrderDetail.builder()
			.productId(product1.getProductId())
			.quantity(150L)
			.productPrice(500000L)
			.build();

		Order order1 = Order.builder()
			.userId(1L)
			.status(OrderStatus.PAID)
			.orderDetails(List.of(orderDetail1))
			.build();
		orderJpaRepository.save(order1);

		OrderDetail orderDetail2 = OrderDetail.builder()
			.productId(product3.getProductId())
			.quantity(100L)
			.productPrice(1500000L)
			.build();

		Order order2 = Order.builder()
			.userId(2L)
			.status(OrderStatus.PAID)
			.orderDetails(List.of(orderDetail2))
			.build();
		orderJpaRepository.save(order2);
	}

	@Test
	@DisplayName("[GET] /api/v1/products - 상품 목록 조회 성공 테스트")
	void getProductsSuccessTest() {
		// when & then
		given()
			.contentType(ContentType.JSON)
			.when()
			.get("/api/v1/products?page=1&size=10")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.items.size()", equalTo(3))
			.body("data.items.find { it.name == 'Apple iPad' }.price", equalTo(500000))
			.body("data.items.find { it.name == 'Galaxy Tab' }.price", equalTo(400000))
			.body("data.items.find { it.name == 'MacBook Air' }.price", equalTo(1500000))
			.body("data.pageInfo.currentPage", equalTo(1))
			.body("data.pageInfo.size", equalTo(10))
			.body("data.pageInfo.totalItems", equalTo(3));
	}

	@Test
	@DisplayName("[GET] /api/v1/products/top-sellers - 상위 판매 상품 조회 성공 테스트")
	void getTopSellersSuccessTest() {
		// when & then
		given()
			.contentType(ContentType.JSON)
			.when()
			.get("/api/v1/products/top-sellers?days=3")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.body("data.topSellers.size()", equalTo(2))
			.body("data.periodDays", equalTo(3))
			.body("data.topSellers[0].name", equalTo("Apple iPad"))
			.body("data.topSellers[0].totalSold", equalTo(150))
			.body("data.topSellers[1].name", equalTo("MacBook Air"))
			.body("data.topSellers[1].totalSold", equalTo(100));
	}

	@Test
	@DisplayName("[GET] /api/v1/products/top-sellers?days=0 - Validation 실패 테스트")
	void getTopSellersWithInvalidDays() {
		// when & then
		given()
			.contentType(ContentType.JSON)
			.when()
			.get("/api/v1/products/top-sellers?days=0")
			.then()
			.log().all()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.body("error.code", equalTo("E400"))
			.body("error.message", containsString("잘못된 요청 정보를 전송하셨습니다"))
			.body("error.data", containsString("days는 3 이상의 값"));
	}
}
