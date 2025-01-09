package kr.hhplus.be.server.domain.balanace;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BalanceLoader {

	private final BalanceRepository balanceRepository;

	@Transactional
	public Balance loadByUserId(Long userId) {

		return balanceRepository.findByUserId(userId)
			.orElseThrow(() -> new ApiException(ErrorType.BALANCE_NOT_FOUND));

	}

}
