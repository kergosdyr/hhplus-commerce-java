package kr.hhplus.be.server.domain.balanace;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.user.UserFinder;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BalanceService {

	private final BalanceModifier balanceModifier;
	private final BalanceFinder balanceFinder;
	private final UserFinder userFinder;

	public Balance charge(long userId, Long amount) {

		if (userFinder.notExistsByUserId(userId)) {
			throw new ApiException(ErrorType.USER_NOT_FOUND);
		}

		return balanceModifier.charge(userId, amount);

	}

	public Balance get(long userId) {

		if (userFinder.notExistsByUserId(userId)) {
			throw new ApiException(ErrorType.USER_NOT_FOUND);
		}

		return balanceFinder.findByUserId(userId);

	}

}
