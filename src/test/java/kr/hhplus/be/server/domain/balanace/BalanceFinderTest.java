package kr.hhplus.be.server.domain.balanace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.error.ApiException;

@ExtendWith(MockitoExtension.class)
class BalanceFinderTest {

	@Mock
	BalanceRepository balanceRepository;

	@InjectMocks
	BalanceFinder balanceFinder;

	@Test
	@DisplayName("잔액이 존재하지 않는다면 ApiException이 발생한다")
	void shouldThrowApiExceptionWhenBalanceNotFound() {

		// given
		given(balanceRepository.findByUserId(1L)).willReturn(Optional.empty());

		// when
		assertThatThrownBy(() -> {
			balanceFinder.findByUserId(1L);
		}).isInstanceOf(ApiException.class).hasMessageContaining("잔액 충전 처리 중 오류가 발생했습니다.");

	}

	@Test
	@DisplayName("잔액이 존재한다면, 잔액(Balance)를 반환한다")
	void shouldReturnBalanceWhenBalanceExists() {
		//given
		Balance givenBalance = Balance.builder()
			.userId(1L)
			.balanceId(1L)
			.amount(1000L)
			.build();
		given(balanceRepository.findByUserId(1L)).willReturn(Optional.of(givenBalance));

		//when
		assertThat(balanceFinder.findByUserId(1L)).isEqualTo(givenBalance);

	}

}