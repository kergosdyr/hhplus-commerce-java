package kr.hhplus.be.server.domain.balanace;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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


}
