package kr.hhplus.be.server.domain.balanace;

import static kr.hhplus.be.server.config.TestUtil.getTestBalance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.config.TestUtil;
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

		User user = TestUtil.createTestUser();

		User savedUser = userJpaRepository.save(user);

		//when & then
		assertThatThrownBy(() -> balanceService.charge(userId, savedBalance.getAmount())).isInstanceOf(
			ApiException.class).hasMessageContaining("요청하신 유저를 찾을 수 없습니다");
	}

	@Test
	@DisplayName("잔액을 충전(charge)하고 User가 유효하다면 충전한 금액만큼 잔액이 증가한다")
	void shouldChargeAmountWhenUserIsValid() {
		//given
		User user = TestUtil.createTestUser();

		User savedUser = userJpaRepository.save(user);
		Balance balance = getTestBalance(savedUser.getUserId(), 1000L);
		balanceJpaRepository.save(balance);
		//then
		Balance chargedBalance = balanceService.charge(balance.getUserId(), 1000L);
		assertThat(chargedBalance.getAmount()).isEqualTo(2000L);

	}

	@Test
	@DisplayName("잔액을 get 할때 유저가 없으면 ApiException(USER_NOT_FOUND) 이 발생한다")
	void shouldThrowApiExceptionWhenUserNotFound() {
		//given
		Balance balance = getTestBalance(1L, 1000L);

		//when
		balanceJpaRepository.save(balance);

		assertThatThrownBy(() -> {
			balanceService.get(-1L);
		}).isInstanceOf(ApiException.class).hasMessageContaining("요청하신 유저를 찾을 수 없습니다");

		//then
	}

	@Test
	@DisplayName("잔액을 get 할때 유저가 존재한다면 해당 유저의 잔액을 가져온다")
	void shouldGetBalanceWhenUserExists() {
		//given
		User user = TestUtil.createTestUser();
		User savedUser = userJpaRepository.save(user);
		Balance balance = getTestBalance(savedUser.getUserId(), 1000L);

		//when
		Balance savedBalance = balanceJpaRepository.save(balance);
		Balance gettedBalance = balanceService.get(user.getUserId());

		//then
		assertThat(gettedBalance.getBalanceId()).isEqualTo(savedBalance.getBalanceId());
	}

}
