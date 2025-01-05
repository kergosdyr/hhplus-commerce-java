package kr.hhplus.be.server.domain.balanace;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BalanceTest {

	@Test
	@DisplayName("charge() 호출 시 기존 잔액에 입력 금액이 더해진다.")
	void shouldIncreaseAmountWhenChargeCalled() {
		// given
		Balance balance = Balance.builder()
			.balanceId(1L)
			.userId(100L)
			.amount(1000L)
			.build();

		// when
		balance.charge(500L);

		// then
		assertThat(balance.getAmount()).isEqualTo(1500L);
	}
}
