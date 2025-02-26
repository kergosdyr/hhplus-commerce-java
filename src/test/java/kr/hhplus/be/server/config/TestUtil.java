package kr.hhplus.be.server.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import kr.hhplus.be.server.domain.balanace.Balance;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDetail;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.enums.CouponStatus;
import kr.hhplus.be.server.enums.OrderStatus;
import kr.hhplus.be.server.enums.ProductStatus;
import kr.hhplus.be.server.enums.RedisKeyPrefix;
import kr.hhplus.be.server.enums.UserCouponStatus;

public abstract class TestUtil {

	public static Balance createTestBalance() {
		return Balance.builder()
			.balanceId(1L)
			.userId(100L)
			.amount(1000L)
			.build();
	}

	public static Balance createTestBalance(long userId, long amount) {
		return Balance.builder()
			.userId(userId)
			.amount(amount)
			.build();
	}


	public static User createTestUser() {
		return User.builder()
			.name("전진")
			.build();
	}

	public static Coupon createTestCoupon(LocalDateTime expiredAt) {
		return Coupon.builder()
			.amount(1000L)
			.expiredAt(expiredAt)
			.name("전진 쿠폰")
			.status(CouponStatus.ACTIVE)
			.build();
	}

	public static Coupon createTestCouponWithInventory(LocalDateTime expiredAt) {
		return Coupon.builder()
			.amount(1000L)
			.expiredAt(expiredAt)
			.name("전진 쿠폰")
			.status(CouponStatus.ACTIVE)
			.build();
	}

	public static UserCoupon createTestUserCoupon(long userId, long couponId) {
		return UserCoupon.builder()
			.couponId(couponId)
			.userId(userId)
			.amount(1000L)
			.issuedAt(LocalDateTime.of(2024, 12, 31, 0, 0))
			.expiredAt(LocalDateTime.of(2025, 1, 2, 0, 0))
			.status(UserCouponStatus.AVAILABLE)
			.build();
	}

	public static Balance getTestBalance(long userId, long amount) {
		return Balance.builder()
			.amount(amount)
			.userId(userId)
			.build();
	}

	public static ProductStock createTestProductStock(long productId, long stock) {
		return ProductStock.builder()
			.productId(productId)
			.stock(stock)
			.build();
	}

	public static Product createTestProduct(String name, long price) {
		return Product.builder()
			.name(name)
			.price(price)
			.status(ProductStatus.AVAILABLE)
			.build();
	}

	public static Order createMockOrder(long userId, List<OrderDetail> orderDetails) {

		return Order.builder()
			.userId(userId)
			.status(OrderStatus.UNPAID)
			.orderDetails(orderDetails)
			.build();
	}

	public static List<OrderDetail> createMockOrderDetails(int detailsSize, long productPrice, long quantity) {

		return LongStream.rangeClosed(1, detailsSize).mapToObj(i -> OrderDetail.builder()
			.productId(i)
			.quantity(quantity)
			.productPrice(productPrice)
			.build()).collect(Collectors.toList());
	}

	public static <T> T proxy(Object aspectInstance, T target) {
		AspectJProxyFactory factory = new AspectJProxyFactory(target);
		factory.addAspect(aspectInstance);
		return factory.getProxy();
	}

	public static RAtomicLong addAndGetCouponCount(RedissonClient redissonClient, long couponId, long count) {
		RAtomicLong couponCounter = redissonClient.getAtomicLong(RedisKeyPrefix.COUPON.getKey(couponId));
		couponCounter.addAndGet(count);
		return couponCounter;
	}

}
