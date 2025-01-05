package kr.hhplus.be.server.domain.balanace;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BalanceModifier {

	private final BalanceLoader balanceLoader;

	public Balance charge(long userId, long amount) {
		var balance = balanceLoader.load(userId);

		balance.charge(amount);
		return balance;
	}

	public Balance use(long userId, long amount) {
		var balance = balanceLoader.load(userId);

		if (!balance.isUsable(amount)) {
			throw new ApiException(ErrorType.BALANCE_OVER_USE);
		}

		balance.use(amount);
		return balance;

	}
}
