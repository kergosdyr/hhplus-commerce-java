package kr.hhplus.be.server.domain.balanace;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserLoader;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

	@Mock
	UserLoader userLoader;

	@Mock
	BalanceModifier balanceModifier;

	@Mock
	BalanceLoader balanceLoader;

	@InjectMocks
	BalanceService balanceService;

}