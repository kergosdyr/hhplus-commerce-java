package kr.hhplus.be.server.domain.balanace;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BalanceModifier {

	private final BalanceFinder balanceFinder;

	@Transactional
	public Balance charge(long userId, long amount) {
		var balance = balanceFinder.findByUserId(userId);

		balance.charge(amount);
		return balance;
	}

	@Transactional
	public Balance use(long userId, long amount) {
		var balance = balanceFinder.findByUserId(userId);

		if (!balance.isUsable(amount)) {
			throw new ApiException(ErrorType.BALANCE_OVER_USE);
		}

		balance.use(amount);
		return balance;

	}
}
