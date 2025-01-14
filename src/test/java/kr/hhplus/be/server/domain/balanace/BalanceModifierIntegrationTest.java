package kr.hhplus.be.server.domain.balanace;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.config.ConcurrencyTestUtil;
import kr.hhplus.be.server.config.IntegrationTest;

public class BalanceModifierIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("잔액이 존재하는 경우에 use 를 호출한다면 주어진 금액만큼 잔액을 사용한다.")
	void should() {
		//given
		long userId = 1L;
		Balance savedBalance = balanceJpaRepository.save(Balance.builder()
			.amount(1000L)
			.userId(userId)
			.build());

		//when
		Balance usedBalance = balanceModifier.use(savedBalance.getUserId(), 999L);

		//then
		assertThat(usedBalance.getAmount()).isEqualTo(1L);
	}

	@Test
	@DisplayName("잔액이 존재하는 경우에 charge를 호출한다면 주어진 금액만큼 잔액을 충전한다")
	void shouldChargeAmountWhenAmountIsExist() {
		//given
		long userId = 1L;
		Balance savedBalance = balanceJpaRepository.save(Balance.builder()
			.amount(1000L)
			.userId(userId)
			.build());

		//when
		Balance usedBalance = balanceModifier.charge(savedBalance.getUserId(), 999L);

		//then
		assertThat(usedBalance.getAmount()).isEqualTo(1999L);
	}

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
