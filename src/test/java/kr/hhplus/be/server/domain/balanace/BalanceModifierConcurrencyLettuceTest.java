package kr.hhplus.be.server.domain.balanace;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import kr.hhplus.be.server.config.ConcurrencyTestUtil;
import kr.hhplus.be.server.config.IntegrationTest;

@TestPropertySource(properties = {"spring.profiles.active=test,lettuce"})
public class BalanceModifierConcurrencyLettuceTest extends IntegrationTest {

	@Test
	@DisplayName("같은 유저가 10회 동시에 잔고를 충전하더라도 정확한 금액 만큼 충전되어야 한다.")
	void shouldConcurrent() throws InterruptedException {
		//given
		long userId = 1L;
		Balance savedBalance = balanceJpaRepository.save(Balance.builder()
			.amount(1000L)
			.userId(userId)
			.build());
		//when
		var result = ConcurrencyTestUtil.run(10, () -> {
			try {
				balanceModifier.charge(savedBalance.getUserId(), 10L);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		});
		Balance balance = balanceJpaRepository.findById(savedBalance.getBalanceId()).get();
		//then
		assertThat(result.success()).isEqualTo(10);
		assertThat(result.fail()).isEqualTo(0);
		assertThat(balance.getAmount()).isEqualTo(1100L);

	}

	@Test
	@DisplayName("같은 유저가 10회 동시에 잔고를 차감하더라도 정확한 금액 만큼 차감되어야 한다.")
	void shouldConcurrentUse() throws InterruptedException {
		//given
		long userId = 1L;
		Balance savedBalance = balanceJpaRepository.save(Balance.builder()
			.amount(1000L)
			.userId(userId)
			.build());
		//when
		var result = ConcurrencyTestUtil.run(10, () -> {
			try {
				balanceModifier.use(savedBalance.getUserId(), 10L);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		});
		Balance balance = balanceJpaRepository.findById(savedBalance.getBalanceId()).get();
		//then
		assertThat(result.success()).isEqualTo(10);
		assertThat(result.fail()).isEqualTo(0);
		assertThat(balance.getAmount()).isEqualTo(900L);

	}

	@Test
	@DisplayName("같은 유저가 10회  동시에 잔고를 차감, 사용하더라도 정확한 금액 만큼 차감되어야 한다.")
	void shouldConcurrentUseAndCharge() throws InterruptedException {
		//given
		long userId = 1L;
		Balance savedBalance = balanceJpaRepository.save(Balance.builder()
			.amount(1000L)
			.userId(userId)
			.build());
		//when
		var result = ConcurrencyTestUtil.run(10, () -> {
			try {
				balanceModifier.charge(savedBalance.getUserId(), 10L);
				balanceModifier.use(savedBalance.getUserId(), 10L);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		});
		Balance balance = balanceJpaRepository.findById(savedBalance.getBalanceId()).get();
		//then
		assertThat(result.success()).isEqualTo(10);
		assertThat(result.fail()).isEqualTo(0);
		assertThat(balance.getAmount()).isEqualTo(1000L);

	}

}
