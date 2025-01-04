package kr.hhplus.be.server.domain.balanace;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BalanceModifier {

	private final BalanceRepository balanceRepository;


	public Balance charge(Long userId, Long amount) {
		var balance = balanceRepository.findByUserId(userId)
			.orElseThrow(() -> new ApiException(ErrorType.BALANCE_NOT_FOUND));

		balance.charge(amount);
		return balance;
	}

}
