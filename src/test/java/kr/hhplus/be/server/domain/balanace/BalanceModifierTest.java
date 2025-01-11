package kr.hhplus.be.server.domain.balanace;

import static kr.hhplus.be.server.config.TestUtil.createTestBalance;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BalanceModifierTest {

	@Mock
	BalanceLoader balanceLoader;

	@InjectMocks
	BalanceModifier balanceModifier;

	@Test
	@DisplayName("잔액이 존재하는 경우에 charge 를 호출한다면 주어진 금액만큼 잔액을 충전한다.")
	void shouldChargeBalance() {

		// given
		when(balanceLoader.loadByUserId(1L)).thenReturn(
			createTestBalance()
		);

		// when
		Balance charge = balanceModifier.charge(1L, 1000L);

		// then
		assertThat(charge.getAmount()).isEqualTo(2000L);

	}

	@Test
	@DisplayName("잔액이 존재하는 경우에 use 를 호출하면 주어진 금액만큼 잔액을 차감한다")
	void shouldUseWhenBalanceExist() {

		//given
		given(balanceLoader.loadByUserId(1L)).willReturn(createTestBalance());

		//when
		Balance usedBalance = balanceModifier.use(1L, 1000L);

		//then
		assertThat(usedBalance.getAmount()).isEqualTo(0L);

	}

}