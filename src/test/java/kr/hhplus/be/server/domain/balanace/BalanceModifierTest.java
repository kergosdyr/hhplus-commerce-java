package kr.hhplus.be.server.domain.balanace;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.error.ApiException;

@ExtendWith(MockitoExtension.class)
class BalanceModifierTest {

	@Mock
	BalanceRepository balanceRepository;

	@InjectMocks
	BalanceModifier balanceModifier;

	@Test
	@DisplayName("잔액(Balance)이 존재하지 않으면, ApiException 발생시킨다.")
	void shouldBalanceNotExistThrowApiException() {

		// given
		Mockito.when(balanceRepository.findByUserId(1L)).thenReturn(Optional.empty());

		// when
		// then
		assertThatThrownBy(() -> {
			balanceModifier.charge(1L, 1000L);
		}).isInstanceOf(ApiException.class).hasMessageContaining("잔액 충전 처리 중 오류가 발생했습니다.");

	}


	@Test
	@DisplayName("잔액이 존재하는 경우에 balanceModifier의 charge 를 호출한다면 주어진 금액만큼 잔액을 충전한다.")
	void shouldChargeBalance() {

		// given
		Mockito.when(balanceRepository.findByUserId(1L))
			.thenReturn(Optional.of(Balance.builder().balanceId(1L).amount(1000L).build()));

		// when
		Balance charge = balanceModifier.charge(1L, 1000L);

		// then
		assertThat(charge.getAmount()).isEqualTo(2000L);

	}

}