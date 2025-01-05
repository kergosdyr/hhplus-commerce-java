package kr.hhplus.be.server.infra.storage.balance;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.balanace.Balance;
import kr.hhplus.be.server.domain.balanace.BalanceRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BalanceRepositoryImpl implements BalanceRepository {

	private final BalanceJpaRepository balanceJpaRepository;

	@Override
	public Optional<Balance> findByUserId(long userId) {
		return balanceJpaRepository.findByUserIdWithLock(userId);
	}

}
