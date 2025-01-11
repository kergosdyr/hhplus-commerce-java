package kr.hhplus.be.server.domain.balanace;

import static kr.hhplus.be.server.config.TestUtil.createTestBalance;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BalanceTest {

	@Test
	@DisplayName("Balance를 Builder 로 생성할 때 원하는 값이 입력된 대로 생성되어야한다")
	void shouldBalanceCreatedWithBuilderMustCreatedByInputValue() {
		//given
		Balance balance = Balance.builder()
			.balanceId(1L)
			.amount(23L)
			.userId(4L)
			.build();

		//when & then
		assertThat(balance)
			.extracting("balanceId", "amount", "userId")
			.containsExactly(1L, 23L, 4L);
	}

	@Test
	@DisplayName("charge() 호출 시 기존 잔액에 입력 금액이 더해진다.")
	void shouldIncreaseAmountWhenChargeCalled() {
		// given
		Balance balance = createTestBalance();

		// when
		balance.charge(500L);

		// then
		assertThat(balance.getAmount()).isEqualTo(1500L);
	}

	@Test
	@DisplayName("use 호출 시 기존 잔액에 입력 금액이 차감된다.")
	void shouldDecreaseAmountWhenUseCalled() {

		//given
		var balance = createTestBalance();

		//when
		balance.use(500L);

		//then

		assertThat(balance.getAmount()).isEqualTo(500L);

	}

	@Test
	@DisplayName("현재 잔액보다 많은 amount 를 isUsable 과 호출하는 경우 false 를 리턴한다")
	void shouldReturnFalseWhenIsUsableCalledWithGreaterAmount() {
		//given
		var balance = createTestBalance();

		//when
		var result = balance.isUsable(1001L);

		//then
		assertThat(result).isEqualTo(false);
	}

	@Test
	@DisplayName("현재 잔액보다 적은 amount 를 isUsable 과 호출하는 경우 true 를 리턴한다")
	void shouldReturnTrueWhenIsUsableCalledWithSmallerAmount() {
		//given
		var balance = createTestBalance();

		//when
		var result = balance.isUsable(999L);

		//then
		assertThat(result).isEqualTo(true);
	}
}
