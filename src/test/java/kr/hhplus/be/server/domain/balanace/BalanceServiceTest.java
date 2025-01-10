package kr.hhplus.be.server.domain.balanace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.user.UserFinder;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

	@Mock
	UserFinder userFinder;

	@Mock
	BalanceModifier balanceModifier;

	@Mock
	BalanceFinder balanceFinder;

	@InjectMocks
	BalanceService balanceService;

	@Test
	@DisplayName("잔액을 충전(charge)할때, User가 유효하지 않으면 ApiException 을 발생시킨다")
	void shouldThrowApiExceptionWhenChargeAndUserNotFound() {

		// given
		long userId = 1L;
		long amount = 1000L;

		when(userFinder.notExistsByUserId(userId)).thenReturn(true);


		// when
		assertThatThrownBy(() -> balanceService.charge(userId, amount))
			.isInstanceOf(ApiException.class)
			.hasMessage(ErrorType.USER_NOT_FOUND.getMessage());

	}

	@Test
	@DisplayName("잔액을 충전(charge)할때, User가 유효하면, BalanceModifier.charge()를 호출한다")
	void shouldChargeBalanceWhenUserValid() {

		// given
		long userId = 1L;
		long amount = 1000L;

		Balance givenBalance = Balance.builder()
			.userId(userId)
			.amount(1000L)
			.build();

		when(userFinder.notExistsByUserId(userId)).thenReturn(false);

		given(balanceModifier.charge(userId, amount)).willReturn(givenBalance);

		//when
		assertThat(balanceService.charge(userId, amount)).isEqualTo(givenBalance);
		verify(balanceModifier, times(1)).charge(userId, amount);

	}

	@Test
	@DisplayName("잔액을 조회(getBalance)할때, User가 유효하지 않으면 ApiException 을 발생시킨다")
	void shouldThrowApiExceptionWhenGenBalanceAndUserNotValid() {

		// given
		long userId = 1L;

		when(userFinder.notExistsByUserId(userId)).thenReturn(true);

		// when
		assertThatThrownBy(() -> balanceService.get(userId))
			.isInstanceOf(ApiException.class)
			.hasMessage(ErrorType.USER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("잔액을 조회(get)할때, User가 유효하면, BalanceLoader.get()를 호출한다")
	void shouldGetBalanceWhenUserValid() {

		// given
		long userId = 1L;

		Balance givenBalance = Balance.builder()
			.userId(userId)
			.amount(1000L)
			.build();

		when(userFinder.notExistsByUserId(userId)).thenReturn(false);

		given(balanceFinder.findByUserId(userId)).willReturn(givenBalance);

		// when
		assertThat(balanceService.get(userId)).isEqualTo(givenBalance);
		verify(balanceFinder, times(1)).findByUserId(userId);
	}


}