package kr.hhplus.be.server.domain.balanace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.error.ApiException;

class BalanceReaderIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("load 를 호출하는 경우 해당하는 userId 의 balance 값을 가져온다")
	void shouldFindBalanceWhenCalledReadByUserIdWithId() {

		//given
		Balance savedBalance = balanceJpaRepository.save(Balance.builder()
			.amount(1000L)
			.userId(1L)
			.build());

		//when
		Balance loadedBalance = balanceReader.readByUserId(savedBalance.getUserId());

		assertThat(loadedBalance.getBalanceId()).isEqualTo(savedBalance.getBalanceId());
		assertThat(loadedBalance.getAmount()).isEqualTo(savedBalance.getAmount());
		assertThat(loadedBalance.getUserId()).isEqualTo(savedBalance.getUserId());

	}

	@Test
	@DisplayName("load 호출 시 해당하는 userId 의 balance 값이 없다면 ApiExcepction(ErroType BALANCE_NOT_FOUND) 를 발생시킨다.")
	void shouldThrowExceptionWhenReadByUserIdNotFound() {

		//given
		Balance savedBalance = balanceJpaRepository.save(Balance.builder()
			.amount(1000L)
			.userId(1L)
			.build());

		//when

		assertThatThrownBy(() -> balanceReader.readByUserId(savedBalance.getUserId() + 2L)).isInstanceOf(
			ApiException.class).hasMessageContaining("잔액 충전 처리 중 오류가 발생했습니다.");

	}

}