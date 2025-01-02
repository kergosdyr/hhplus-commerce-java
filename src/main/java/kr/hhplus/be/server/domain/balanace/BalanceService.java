package kr.hhplus.be.server.domain.balanace;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.user.UserLoader;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BalanceService {

	private final UserLoader userLoader;
	private final BalanceModifier balanceModifier;
	private final BalanceLoader balanceLoader;

	public long charge(long userId, Long amount) {

		var user = userLoader.load(userId);

		return balanceModifier.charge(user.getUserId(), amount);

	}

	public long getBalanceAmount(long userId) {
		return balanceLoader.load(userId).getAmount();
	}
}
