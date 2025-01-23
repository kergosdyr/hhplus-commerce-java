package kr.hhplus.be.server.support;

import static kr.hhplus.be.server.config.TestUtil.createMockOrder;
import static kr.hhplus.be.server.config.TestUtil.createMockOrderDetails;
import static kr.hhplus.be.server.config.TestUtil.createTestBalance;
import static kr.hhplus.be.server.config.TestUtil.createTestProductStock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.domain.analytics.AnalyticsSender;
import kr.hhplus.be.server.domain.balanace.BalanceFinder;
import kr.hhplus.be.server.domain.balanace.BalanceModifier;
import kr.hhplus.be.server.domain.coupon.CouponApplier;
import kr.hhplus.be.server.domain.coupon.UserCouponFinder;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderReader;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentProcessor;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.product.ProductStockModifier;
import kr.hhplus.be.server.domain.product.ProductStockRepository;

@ExtendWith(MockitoExtension.class)
class WithLockAspectTest {

	static String TEST_UUID = "13d2e356-a1bf-4521-92e3-56a1bfe521fc";

	@Mock
	LockManager lockManager;

	@Mock
	BalanceFinder balanceFinder;

	@InjectMocks
	BalanceModifier balanceModifier;

	@Mock
	PaymentRepository paymentRepository;

	@Mock
	OrderReader orderReader;

	@Mock
	UserCouponFinder userCouponFinder;

	@Mock
	ProductStockRepository productStockRepository;

	@InjectMocks
	WithLockAspect withLockAspect;

	@Mock
	CouponApplier couponApplier;

	@InjectMocks
	ProductStockModifier stockModifier;

	@Test
	@DisplayName("BalanceModifier.use 메서드 호출 시 balance:balanceId 형태로 Lock 을 획득/해제한다")
	void testBalanceModifierUseLock() {
		//given
		when(balanceFinder.findByUserId(anyLong())).thenReturn(createTestBalance());
		when(lockManager.acquire(anyString())).thenReturn(TEST_UUID);

		BalanceModifier proxiedBalanceModifier = TestUtil.proxy(withLockAspect, balanceModifier);

		//when & then
		startTransaction();
		proxiedBalanceModifier.use(1L, 1000L);

		verify(lockManager).acquire("balance:1");
		verify(lockManager, never()).release(anyString());

		endTransaction();
		verify(lockManager).release("balance:1");
	}

	@Test
	@DisplayName("BalanceModifier.charge 메서드 호출 시 balance:balanceId 형태로 Lock 을 획득/해제한다")
	void testBalanceModifierChargeLock() {
		//given
		when(balanceFinder.findByUserId(anyLong())).thenReturn(createTestBalance());
		when(lockManager.acquire(anyString())).thenReturn(TEST_UUID);

		var proxiedBalanceModifier = TestUtil.proxy(withLockAspect, balanceModifier);

		//when & then
		startTransaction();
		proxiedBalanceModifier.charge(1L, 1000L);

		verify(lockManager).acquire("balance:1");
		verify(lockManager, never()).release(anyString());

		endTransaction();
		verify(lockManager).release("balance:1");
	}

	@Test
	@DisplayName("PaymentProcessor.process 메서드 호출 시 user_id:userId:order_id:orderId: 형태로 Lock 획득/해제한다")
	void testPaymentProcessorWithLock() {

		//given
		var paymentProcessor = new PaymentProcessor(
			Mockito.mock(BalanceModifier.class),
			paymentRepository,
			orderReader,
			Mockito.mock(AnalyticsSender.class),
			couponApplier
		);

		when(paymentRepository.save(any())).thenReturn(Payment.builder()
			.paymentId(1L)
			.build());

		var proxiedPaymentProcessor = TestUtil.proxy(withLockAspect, paymentProcessor);

		var mockOrder = createMockOrder(1L, createMockOrderDetails(2, 1000L, 2L));

		//when & then
		when(orderReader.read(123L)).thenReturn(mockOrder);
		when(lockManager.acquire(anyString())).thenReturn(TEST_UUID);

		startTransaction();
		proxiedPaymentProcessor.process(1, 123L);

		verify(lockManager).acquire("user_id:1:order_id:123");
		verify(lockManager, never()).release(anyString());

		endTransaction();
		verify(lockManager).release("user_id:1:order_id:123");
	}

	@Test
	@DisplayName("PaymentProcessor.processWithCoupon 메서드 호출 시 user_id:userId:order_id:orderId: 형태로 Lock 획득/해제한다")
	void testPaymentProcessorWithCouponWithLock() {

		//given
		var paymentProcessor = new PaymentProcessor(
			Mockito.mock(BalanceModifier.class),
			paymentRepository,
			orderReader,
			Mockito.mock(AnalyticsSender.class),
			couponApplier
		);

		when(paymentRepository.save(any())).thenReturn(Payment.builder()
			.paymentId(1L)
			.build());

		var proxiedPaymentProcessor = TestUtil.proxy(withLockAspect, paymentProcessor);

		var mockOrder = createMockOrder(1L, createMockOrderDetails(2, 1000L, 2L));

		//when & then
		when(orderReader.read(123L)).thenReturn(mockOrder);
		when(lockManager.acquire(anyString())).thenReturn(TEST_UUID);
		when(couponApplier.apply(anyLong(), anyLong(), anyLong())).thenReturn(900L);

		startTransaction();
		proxiedPaymentProcessor.processWithCoupon(1, 1L, 123L);

		verify(lockManager).acquire("user_id:1:order_id:123");
		verify(lockManager, never()).release(anyString());

		endTransaction();
		verify(lockManager).release("user_id:1:order_id:123");
	}

	@Test
	@DisplayName("CouponApplier.apply 호출 시 user_coupon:couponId 로 Lock 획득/해제한다")
	void testCouponApplierLock() {
		//given
		var proxiedCouponApplier = TestUtil.proxy(withLockAspect, couponApplier);

		when(lockManager.acquire(anyString())).thenReturn(TEST_UUID);

		//when & then
		startTransaction();
		proxiedCouponApplier.apply(5000L, 1L, 2L);

		verify(lockManager).acquire("user_coupon:2");
		verify(lockManager, never()).release(anyString());

		endTransaction();
		verify(lockManager).release("user_coupon:2");
	}

	@Test
	@DisplayName("ProductStockModifier.sell (keys=리스트) 호출 시 product:...로 시작하는 키들을 Lock 획득/해제한다")
	void testProductStockModifierMultiKeys() {
		//given
		var proxiedStockModifier = TestUtil.proxy(withLockAspect, stockModifier);

		when(productStockRepository.findByProductId(1L)).thenReturn(
			Optional.of(createTestProductStock(1L, 10)));
		when(productStockRepository.findByProductId(2L))
			.thenReturn(Optional.of(createTestProductStock(2L, 10)));
		when(lockManager.acquire(anyString())).thenReturn(TEST_UUID);

		var orderCommands = List.of(
			new OrderCommand(1L, 2),
			new OrderCommand(2L, 1)
		);
		//when & then
		startTransaction();

		proxiedStockModifier.sell(orderCommands);

		verify(lockManager).acquire("product:1");
		verify(lockManager).acquire("product:2");
		verify(lockManager, never()).release(anyString());

		endTransaction();
		verify(lockManager).release("product:1");
		verify(lockManager).release("product:2");
	}

	private void endTransaction() {
		TransactionSynchronizationManager.getSynchronizations()
			.forEach(sync -> sync.afterCompletion(TransactionSynchronization.STATUS_COMMITTED));
		TransactionSynchronizationManager.clearSynchronization();
	}

	private void startTransaction() {
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.initSynchronization();
		}
	}
}
