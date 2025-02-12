package kr.hhplus.be.server.support;

import static kr.hhplus.be.server.config.TestUtil.createMockOrder;
import static kr.hhplus.be.server.config.TestUtil.createMockOrderDetails;
import static kr.hhplus.be.server.config.TestUtil.createTestBalance;
import static kr.hhplus.be.server.config.TestUtil.createTestProductStock;
import static kr.hhplus.be.server.config.TestUtil.proxy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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

import kr.hhplus.be.server.domain.balanace.BalanceModifier;
import kr.hhplus.be.server.domain.balanace.BalanceReader;
import kr.hhplus.be.server.domain.coupon.CouponApplier;
import kr.hhplus.be.server.domain.coupon.CouponIssuer;
import kr.hhplus.be.server.domain.coupon.CouponPublisher;
import kr.hhplus.be.server.domain.coupon.CouponReader;
import kr.hhplus.be.server.domain.coupon.UserCouponValidator;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderReader;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.ProductStockModifier;
import kr.hhplus.be.server.domain.product.ProductStockRepository;

@ExtendWith(MockitoExtension.class)
class WithLockAspectTest {

	@Mock
	LockManager lockManager;

	@Mock
	BalanceReader balanceReader;

	@InjectMocks
	BalanceModifier balanceModifier;

	@Mock
	PaymentRepository paymentRepository;

	@Mock
	OrderReader orderReader;

	@Mock
	ProductStockRepository productStockRepository;

	@InjectMocks
	WithLockAspect withLockAspect;

	@Mock
	CouponApplier couponApplier;

	@InjectMocks
	ProductStockModifier stockModifier;

	@Mock
	UserCouponValidator userCouponValidator;

	@Mock
	CouponReader couponReader;

	@Mock
	CouponPublisher couponPublisher;

	@InjectMocks
	CouponIssuer couponIssuer;


	@Test
	@DisplayName("BalanceModifier.use 메서드 호출 시 balance:balanceId 형태로 Lock 을 획득하고 트랜잭션이 이 COMMIT 된 이후에 release 한다")
	void testBalanceModifierUseLockWithCommit() {
		//given
		when(balanceReader.readByUserId(anyLong())).thenReturn(createTestBalance());

		BalanceModifier proxiedBalanceModifier = proxy(withLockAspect, balanceModifier);

		//when & then
		startTransaction();
		proxiedBalanceModifier.use(1L, 1000L);

		verify(lockManager).acquireAll(List.of("balance:1"));
		verify(lockManager, never()).releaseAll(anyList());

		commitTransaction();
		verify(lockManager).releaseAll(List.of("balance:1"));
	}

	@Test
	@DisplayName("BalanceModifier.use 메서드 호출 시 balance:balanceId 형태로 Lock 을 획득하고 트랜잭션이 이 ROLLBACK 된 이후에도 release 한다")
	void testBalanceModifierUseLockWithRollback() {
		//given
		when(balanceReader.readByUserId(anyLong())).thenReturn(createTestBalance());

		BalanceModifier proxiedBalanceModifier = proxy(withLockAspect, balanceModifier);

		//when & then
		startTransaction();
		proxiedBalanceModifier.use(1L, 1000L);

		verify(lockManager).acquireAll(List.of("balance:1"));
		verify(lockManager, never()).releaseAll(anyList());

		rollbackTransaction();
		verify(lockManager).releaseAll(List.of("balance:1"));
	}

	@Test
	@DisplayName("BalanceModifier.charge 메서드 호출 시 balance:balanceId 형태로 Lock 을 획득하고 트랜잭션이 이 COMMIT 된 이후에 release 한다")
	void testBalanceModifierChargeLockWithCommit() {
		//given
		when(balanceReader.readByUserId(anyLong())).thenReturn(createTestBalance());

		var proxiedBalanceModifier = proxy(withLockAspect, balanceModifier);

		//when & then
		startTransaction();
		proxiedBalanceModifier.charge(1L, 1000L);

		verify(lockManager).acquireAll(List.of("balance:1"));
		verify(lockManager, never()).releaseAll(anyList());

		commitTransaction();
		verify(lockManager).releaseAll(List.of("balance:1"));
	}

	@Test
	@DisplayName("BalanceModifier.charge 메서드 호출 시 balance:balanceId 형태로 Lock 을 획득하고 트랜잭션이 이 ROLLBACK 된 이후에도 release 한다")
	void testBalanceModifierChargeLockWithRollback() {
		//given
		when(balanceReader.readByUserId(anyLong())).thenReturn(createTestBalance());

		var proxiedBalanceModifier = proxy(withLockAspect, balanceModifier);

		//when & then
		startTransaction();
		proxiedBalanceModifier.charge(1L, 1000L);

		verify(lockManager).acquireAll(List.of("balance:1"));
		verify(lockManager, never()).releaseAll(anyList());

		rollbackTransaction();
		verify(lockManager).releaseAll(List.of("balance:1"));
	}

	@Test
	@DisplayName("PaymentProcessor.process 메서드 호출 시 user_id:userId:order_id:orderId: 형태로 Lock 획득하고 트랜잭션이 이 COMMIT 된 이후에 release 한다")
	void testPaymentProcessorWithLockCommit() {

		//given
		var paymentProcessor = mockPaymentService();

		when(paymentRepository.save(any())).thenReturn(Payment.builder()
			.paymentId(1L)
			.build());

		var proxiedPaymentProcessor = proxy(withLockAspect, paymentProcessor);

		var mockOrder = createMockOrder(1L, createMockOrderDetails(2, 1000L, 2L));

		//when & then
		when(orderReader.read(123L)).thenReturn(mockOrder);

		startTransaction();
		proxiedPaymentProcessor.pay(1, 123L);

		verify(lockManager).acquireAll(List.of("user_id:1:order_id:123"));
		verify(lockManager, never()).releaseAll(anyList());

		commitTransaction();
		verify(lockManager).releaseAll(List.of("user_id:1:order_id:123"));
	}

	@Test
	@DisplayName("PaymentProcessor.process 메서드 호출 시 user_id:userId:order_id:orderId: 형태로 Lock 획득하고 트랜잭션이 이 ROLLBACK 된 이후에도 release 한다")
	void testPaymentProcessorWithLockRollback() {

		//given
		var paymentProcessor = mockPaymentService();

		when(paymentRepository.save(any())).thenReturn(Payment.builder()
			.paymentId(1L)
			.build());

		var proxiedPaymentProcessor = proxy(withLockAspect, paymentProcessor);

		var mockOrder = createMockOrder(1L, createMockOrderDetails(2, 1000L, 2L));

		//when & then
		when(orderReader.read(123L)).thenReturn(mockOrder);

		startTransaction();
		proxiedPaymentProcessor.pay(1, 123L);

		verify(lockManager).acquireAll(List.of("user_id:1:order_id:123"));
		verify(lockManager, never()).releaseAll(anyList());

		rollbackTransaction();
		verify(lockManager).releaseAll(List.of("user_id:1:order_id:123"));
	}

	@Test
	@DisplayName("PaymentProcessor.processWithCoupon 메서드 호출 시 user_id:userId:order_id:orderId: 형태로 Lock 획득하고 트랜잭션이 이 COMMIT 된 이후에 release 한다")
	void testPaymentProcessorWithCouponWithLockCommit() {

		//given
		var paymentProcessor = mockPaymentService();

		when(paymentRepository.save(any())).thenReturn(Payment.builder()
			.paymentId(1L)
			.build());

		var proxiedPaymentProcessor = proxy(withLockAspect, paymentProcessor);

		var mockOrder = createMockOrder(1L, createMockOrderDetails(2, 1000L, 2L));

		//when & then
		when(orderReader.read(123L)).thenReturn(mockOrder);
		when(couponApplier.apply(anyLong(), anyLong(), anyLong())).thenReturn(900L);

		startTransaction();
		proxiedPaymentProcessor.payWithCoupon(1, 1L, 123L);

		verify(lockManager).acquireAll(List.of("user_id:1:order_id:123"));
		verify(lockManager, never()).releaseAll(anyList());

		commitTransaction();
		verify(lockManager).releaseAll(List.of("user_id:1:order_id:123"));
	}

	@Test
	@DisplayName("PaymentProcessor.processWithCoupon 메서드 호출 시 user_id:userId:order_id:orderId: 형태로 Lock 획득하고 트랜잭션이 이 COMMIT 된 이후에 release 한다")
	void testPaymentProcessorWithCouponWithLockRollback() {

		//given
		var paymentProcessor = mockPaymentService();

		when(paymentRepository.save(any())).thenReturn(Payment.builder()
			.paymentId(1L)
			.build());

		var proxiedPaymentProcessor = proxy(withLockAspect, paymentProcessor);

		var mockOrder = createMockOrder(1L, createMockOrderDetails(2, 1000L, 2L));

		//when & then
		when(orderReader.read(123L)).thenReturn(mockOrder);
		when(couponApplier.apply(anyLong(), anyLong(), anyLong())).thenReturn(900L);

		startTransaction();
		proxiedPaymentProcessor.payWithCoupon(1, 1L, 123L);

		verify(lockManager).acquireAll(List.of("user_id:1:order_id:123"));
		verify(lockManager, never()).releaseAll(anyList());

		rollbackTransaction();
		verify(lockManager).releaseAll(List.of("user_id:1:order_id:123"));
	}

	private PaymentService mockPaymentService() {
		return new PaymentService(
			Mockito.mock(BalanceModifier.class),
			paymentRepository,
			orderReader,
			Mockito.mock(PaymentEventPublisher.class),
			couponApplier
		);
	}

	@Test
	@DisplayName("CouponApplier.apply 호출 시 user_coupon:couponId 로 Lock 획득하고 트랜잭션이 이 COMMIT 된 이후에 release 한다")
	void testCouponApplierLockCommit() {
		//given
		var proxiedCouponApplier = proxy(withLockAspect, couponApplier);

		//when & then
		startTransaction();
		proxiedCouponApplier.apply(5000L, 1L, 2L);

		verify(lockManager).acquireAll(List.of("user_coupon:2"));
		verify(lockManager, never()).releaseAll(anyList());

		commitTransaction();
		verify(lockManager).releaseAll(List.of("user_coupon:2"));
	}

	@Test
	@DisplayName("CouponApplier.apply 호출 시 user_coupon:couponId 로 Lock 획득하고 트랜잭션이 이 ROLLBACK 된 이후에도 release 한다")
	void testCouponApplierLockRollback() {
		//given
		var proxiedCouponApplier = proxy(withLockAspect, couponApplier);

		//when & then
		startTransaction();
		proxiedCouponApplier.apply(5000L, 1L, 2L);

		verify(lockManager).acquireAll(List.of("user_coupon:2"));
		verify(lockManager, never()).releaseAll(anyList());

		rollbackTransaction();
		verify(lockManager).releaseAll(List.of("user_coupon:2"));
	}

	@Test
	@DisplayName("ProductStockModifier.sell 호출 시 리스트 형식으로 Lock 을 획득하고 트랜잭션이 COMMIT 된 이후에 Lock 을 해제한다")
	void testProductStockModifierMultiKeysCommit() {
		//given
		var proxiedStockModifier = proxy(withLockAspect, stockModifier);

		when(productStockRepository.findByProductId(1L)).thenReturn(
			Optional.of(createTestProductStock(1L, 10)));
		when(productStockRepository.findByProductId(2L))
			.thenReturn(Optional.of(createTestProductStock(2L, 10)));

		var orderCommands = List.of(
			new OrderCommand(1L, 2),
			new OrderCommand(2L, 1)
		);
		//when & then
		startTransaction();

		proxiedStockModifier.sell(orderCommands);

		verify(lockManager).acquireAll(List.of("product:2", "product:1"));
		verify(lockManager, never()).releaseAll(anyList());

		commitTransaction();
		verify(lockManager).releaseAll(List.of("product:2", "product:1"));
	}

	@Test
	@DisplayName("ProductStockModifier.sell 호출 시 리스트 형식으로 Lock 을 획득하고 트랜잭션이 ROLLBACK 된 이후에도 Lock 을 해제한다")
	void testProductStockModifierMultiKeysRollback() {
		//given
		var proxiedStockModifier = proxy(withLockAspect, stockModifier);

		when(productStockRepository.findByProductId(1L)).thenReturn(
			Optional.of(createTestProductStock(1L, 10)));
		when(productStockRepository.findByProductId(2L))
			.thenReturn(Optional.of(createTestProductStock(2L, 10)));

		var orderCommands = List.of(
			new OrderCommand(1L, 2),
			new OrderCommand(2L, 1)
		);
		//when & then
		startTransaction();

		proxiedStockModifier.sell(orderCommands);

		verify(lockManager).acquireAll(List.of("product:2", "product:1"));
		verify(lockManager, never()).releaseAll(anyList());

		rollbackTransaction();
		verify(lockManager).releaseAll(List.of("product:2", "product:1"));
	}

	private void commitTransaction() {
		TransactionSynchronizationManager.getSynchronizations()
			.forEach(sync -> sync.afterCompletion(TransactionSynchronization.STATUS_COMMITTED));
		TransactionSynchronizationManager.clearSynchronization();
	}

	private void rollbackTransaction() {
		TransactionSynchronizationManager.getSynchronizations()
			.forEach(sync -> sync.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));
		TransactionSynchronizationManager.clearSynchronization();
	}


	private void startTransaction() {
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.initSynchronization();
		}
	}
}
