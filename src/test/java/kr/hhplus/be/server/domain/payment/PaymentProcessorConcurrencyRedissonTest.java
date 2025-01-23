package kr.hhplus.be.server.domain.payment;

import static kr.hhplus.be.server.config.TestUtil.createMockOrderDetails;
import static kr.hhplus.be.server.config.TestUtil.createTestUser;
import static kr.hhplus.be.server.config.TestUtil.getTestBalance;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import kr.hhplus.be.server.config.ConcurrencyTestUtil;
import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.domain.balanace.Balance;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.user.User;

@TestPropertySource(properties = {"spring.profiles.active=test,redisson"})
class PaymentProcessorConcurrencyRedissonTest extends IntegrationTest {

	@Test
	@DisplayName("유저의 balance가 15000원이고 15000원 결제를 5번 동시 요청 시도할 경우 1번만 성공해야 한다.")
	void shouldProcessOnlyFourPaymentsWhen40ConcurrentRequestsAreMade() throws InterruptedException {
		// given
		User user = createTestUser();
		User savedUser = userJpaRepository.save(user);

		Balance balance = getTestBalance(savedUser.getUserId(), 15_000L);
		Balance savedBalance = balanceJpaRepository.save(balance);
		Order mockOrder = orderJpaRepository.save(
			TestUtil.createMockOrder(savedUser.getUserId(), createMockOrderDetails(3, 5000L, 1)));

		int numberOfRequests = 5;
		var run = ConcurrencyTestUtil.run(numberOfRequests, () -> {
			try {
				paymentProcessor.process(savedUser.getUserId(), mockOrder.getOrderId());

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		});

		assertThat(run.success()).isEqualTo(1);
		assertThat(run.fail()).isEqualTo(4);

		// 5) DB에 반영된 최종 balance가 0원인지 확인
		Balance modifiedBalance = balanceJpaRepository
			.findById(savedBalance.getBalanceId())
			.orElseThrow(RuntimeException::new);

		assertThat(modifiedBalance.getAmount()).isEqualTo(0L);
	}

}