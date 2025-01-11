package kr.hhplus.be.server.domain.balanace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.user.UserValidator;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

	@Mock
	UserValidator userValidator;

	@Mock
	BalanceModifier balanceModifier;

	@Mock
	BalanceLoader balanceLoader;

	@InjectMocks
	BalanceService balanceService;

	@Test
	@DisplayName("잔액을 충전(charge)할때, User가 유효하지 않으면 ApiException 을 발생시킨다")
	void shouldThrowApiExceptionWhenChargeAndUserNotFound() {

		// given
		long userId = 1L;
		long amount = 1000L;

		doThrow(new ApiException(ErrorType.USER_NOT_FOUND)).when(userValidator).validate(userId);

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

		doNothing().when(userValidator).validate(userId);
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

		doThrow(new ApiException(ErrorType.USER_NOT_FOUND)).when(userValidator).validate(userId);

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

		doNothing().when(userValidator).validate(userId);
		given(balanceLoader.loadByUserId(userId)).willReturn(givenBalance);

		// when
		assertThat(balanceService.get(userId)).isEqualTo(givenBalance);
		verify(balanceLoader, times(1)).loadByUserId(userId);
	}


}