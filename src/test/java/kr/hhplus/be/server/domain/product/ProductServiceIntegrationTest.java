package kr.hhplus.be.server.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDetail;
import kr.hhplus.be.server.enums.OrderStatus;
import kr.hhplus.be.server.enums.ProductStatus;

class ProductServiceIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("ProductService 에서 find 를 호출할때 Keyword 를 함께 전달하면 해당하는 keyword 보매면 Product List 를 가져온다")
	void shouldFindProductWithKeywordWhenFindCalled() {

		//given
		Product product1 = productJpaRepository.save(TestUtil.createTestProduct("상품1", 10000L));
		Product product2 = productJpaRepository.save(TestUtil.createTestProduct("상품2", 10000L));
		Product product3 = productJpaRepository.save(TestUtil.createTestProduct("다른3", 10000L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product1.getProductId(), 10L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product2.getProductId(), 10L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product3.getProductId(), 10L));

		//when
		List<Product> products = productService.find("상품", 1, 10);

		//then

		assertThat(products).hasSize(2);
		assertThat(products).extracting("name").containsExactlyInAnyOrder(
			"상품1", "상품2"
		);

	}

	@Test
	@DisplayName("ProductService 에서 find 를 호출할 때 Keyword 없이 호출하면 Keyword 에 상관없이 Product List를 가져온다")
	void shouldFindProductCountWithoutKeywordWHenFindCalled() {
		//given
		Product product1 = productJpaRepository.save(TestUtil.createTestProduct("상품1", 10000L));
		Product product2 = productJpaRepository.save(TestUtil.createTestProduct("상품2", 10000L));
		Product product3 = productJpaRepository.save(TestUtil.createTestProduct("다른3", 10000L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product1.getProductId(), 10L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product2.getProductId(), 10L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product3.getProductId(), 10L));

		//when
		List<Product> products = productService.find("", 1, 10);

		//then
		assertThat(products).hasSize(3);
		assertThat(products).extracting("name").containsExactlyInAnyOrder(
			"상품1", "상품2", "다른3"
		);

	}

	@Test
	@DisplayName("ProductService 에서 findCount 를 호출하면 해당 Keyword 에 해당하는 Count 를 들고온다")
	void shouldFindCountProductWhenFindCountCalled() {
		//given
		Product product1 = productJpaRepository.save(TestUtil.createTestProduct("상품1", 10000L));
		Product product2 = productJpaRepository.save(TestUtil.createTestProduct("상품2", 10000L));
		Product product3 = productJpaRepository.save(TestUtil.createTestProduct("다른3", 10000L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product1.getProductId(), 10L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product2.getProductId(), 10L));
		productStockJpaRepository.save(TestUtil.createTestProductStock(product3.getProductId(), 10L));

		//when
		long count = productService.findCount("상품");

		//then
		assertThat(count).isEqualTo(2);
	}

	@Test
	@DisplayName("ProductService 에서 findAllTopSeller 를 호출하면 days 에 해당하는 값을 들고온다 ")
	void shouldFindAllTopSellerWhenFindAllTopSellerCalled() {

		Product p1 = createProduct("iPhone 13", 1200000L, 50);
		Product p2 = createProduct("Galaxy S21", 900000L, 40);
		Product p3 = createProduct("MacBook Pro", 2500000L, 30);
		Product p4 = createProduct("iPad Pro", 1500000L, 20);
		Product p5 = createProduct("AirPods", 250000L, 100);
		Product p6 = createProduct("Galaxy Buds", 200000L, 80);
		Product p7 = createProduct("Apple Watch", 500000L, 60);
		Product p8 = createProduct("Galaxy Watch", 450000L, 70);
		Product p9 = createProduct("PlayStation 5", 700000L, 15);
		Product p10 = createProduct("Xbox Series X", 650000L, 25);

		List<Product> products = List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

		createOrder(1L, List.of(
			createOrderDetail(p1, 2),
			createOrderDetail(p5, 10)
		));

		createOrder(2L, List.of(
			createOrderDetail(p3, 1),
			createOrderDetail(p8, 2)
		));

		createOrder(3L, List.of(
			createOrderDetail(p4, 9)
		));

		createOrder(4L, List.of(
			createOrderDetail(p2, 2),
			createOrderDetail(p7, 1),
			createOrderDetail(p6, 5)
		));

		createOrder(5L, List.of(
			createOrderDetail(p6, 7),
			createOrderDetail(p9, 1),
			createOrderDetail(p10, 2)
		));

		//when
		List<ProductSellerOutput> allTopSellers = productService.findAllTopSellers(3);

		//then
		assertThat(allTopSellers).hasSize(5);
		assertThat(allTopSellers)
			.extracting("product.name")
			.containsExactly("Galaxy Buds", "AirPods", "iPad Pro", "iPhone 13", "Xbox Series X");
		assertThat(allTopSellers)
			.extracting("sell")
			.containsExactly(12L, 10L, 9L, 2L, 2L);

	}

	@Test
	@DisplayName("ProductService 에서 findAllTopSeller 를 두번 호출하면 같은 days 인 경우 CacheManager 의 값을 사용한다")
	void shouldFindAllTopSellerWhenFindAllTopSellerCalledTwiceThenCalledCache() {

		Product p1 = createProduct("iPhone 13", 1200000L, 50);
		Product p2 = createProduct("Galaxy S21", 900000L, 40);
		Product p3 = createProduct("MacBook Pro", 2500000L, 30);
		Product p4 = createProduct("iPad Pro", 1500000L, 20);
		Product p5 = createProduct("AirPods", 250000L, 100);
		Product p6 = createProduct("Galaxy Buds", 200000L, 80);
		Product p7 = createProduct("Apple Watch", 500000L, 60);
		Product p8 = createProduct("Galaxy Watch", 450000L, 70);
		Product p9 = createProduct("PlayStation 5", 700000L, 15);
		Product p10 = createProduct("Xbox Series X", 650000L, 25);

		List<Product> products = List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);

		createOrder(1L, List.of(
			createOrderDetail(p1, 2),
			createOrderDetail(p5, 10)
		));

		createOrder(2L, List.of(
			createOrderDetail(p3, 1),
			createOrderDetail(p8, 2)
		));

		createOrder(3L, List.of(
			createOrderDetail(p4, 9)
		));

		createOrder(4L, List.of(
			createOrderDetail(p2, 2),
			createOrderDetail(p7, 1),
			createOrderDetail(p6, 5)
		));

		createOrder(5L, List.of(
			createOrderDetail(p6, 7),
			createOrderDetail(p9, 1),
			createOrderDetail(p10, 2)
		));

		//when
		List<ProductSellerOutput> topSellersFromCacheBefore = redissonCacheManager
			.getCache("topSellersCache")
			.get(3, List.class);

		assertThat(topSellersFromCacheBefore).isNull();

		List<ProductSellerOutput> allTopSellers = productService.findAllTopSellers(3);

		//then
		assertThat(allTopSellers).hasSize(5);
		assertThat(allTopSellers)
			.extracting("product.name")
			.containsExactly("Galaxy Buds", "AirPods", "iPad Pro", "iPhone 13", "Xbox Series X");
		assertThat(allTopSellers)
			.extracting("sell")
			.containsExactly(12L, 10L, 9L, 2L, 2L);

		List<ProductSellerOutput> allTopSellers2 = productService.findAllTopSellers(3);
		assertThat(allTopSellers2).hasSize(5);
		assertThat(allTopSellers2)
			.extracting("product.name")
			.containsExactly("Galaxy Buds", "AirPods", "iPad Pro", "iPhone 13", "Xbox Series X");
		assertThat(allTopSellers2)
			.extracting("sell")
			.containsExactly(12L, 10L, 9L, 2L, 2L);

		List<ProductSellerOutput> topSellersFromCache = redissonCacheManager
			.getCache("topSellersCache")
			.get(3, List.class);

		assertThat(topSellersFromCache).hasSize(5);
		assertThat(topSellersFromCache)
			.extracting("product.name")
			.containsExactly("Galaxy Buds", "AirPods", "iPad Pro", "iPhone 13", "Xbox Series X");
		assertThat(topSellersFromCache)
			.extracting("sell")
			.containsExactly(12L, 10L, 9L, 2L, 2L);

		verify(productFinder, times(1)).findAllTopSellers(3);

	}

	private Product createProduct(String name, long price, int stock) {
		Product product = productJpaRepository.save(Product.builder()
			.name(name)
			.price(price)
			.status(ProductStatus.AVAILABLE)
			.build());

		productStockJpaRepository.save(ProductStock.builder()
			.productId(product.getProductId())
			.stock(stock)
			.build());

		return product;
	}

	private OrderDetail createOrderDetail(Product product, long quantity) {
		return OrderDetail.builder()
			.productId(product.getProductId())
			.quantity(quantity)
			.productPrice(product.getPrice())
			.build();
	}

	private void createOrder(Long userId, List<OrderDetail> orderDetails) {
		Order order = Order.builder()
			.userId(userId)
			.status(OrderStatus.PAID)
			.orderDetails(orderDetails)
			.build();

		orderJpaRepository.save(order);
	}

}