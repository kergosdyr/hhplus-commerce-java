package kr.hhplus.be.server.domain.balanace;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.user.UserValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BalanceService {

	private final BalanceModifier balanceModifier;
	private final BalanceLoader balanceLoader;
	private final UserValidator userValidator;

	public Balance charge(long userId, Long amount) {

		userValidator.validate(userId);

		return balanceModifier.charge(userId, amount);

	}

	public long getAmount(long userId) {
		return balanceLoader.load(userId).getAmount();
	}
}
