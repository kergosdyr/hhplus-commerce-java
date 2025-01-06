package kr.hhplus.be.server.domain.balanace;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.error.ApiException;

public class BalanceServiceIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("잔액을 충전(charge)할때, User가 유효하지 않으면 ApiException(USER_NOT_FOUND)을 발생시킨다")
	void shouldChargeAmount() {
		//given
		long userId = -1L;
		Balance savedBalance = balanceJpaRepository.save(Balance.builder()
			.amount(1000L)
			.userId(userId)
			.build());

		User user = User.builder()
			.name("전진")
			.build();

		User savedUser = userJpaRepository.save(user);

		//when & then
		assertThatThrownBy(() -> balanceService.charge(userId, savedBalance.getAmount())).isInstanceOf(
			ApiException.class).hasMessageContaining("요청하신 유저를 찾을 수 없습니다");
	}

}
