package kr.hhplus.be.server.infra.storage.balance;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.balanace.Balance;
import kr.hhplus.be.server.domain.balanace.BalanceRepository;
import kr.hhplus.be.server.infra.storage.config.LettuceLockManager;
import lombok.RequiredArgsConstructor;

@Primary
@Component
@RequiredArgsConstructor
public class BalanceRepositoryLettuceImpl implements BalanceRepository {

	private static final String LETTUCE_LOCK_PREFIX = "BalanceRepositoryLettuceImpl.findByUserId";
	private final BalanceJpaRepository balanceJpaRepository;

	private final LettuceLockManager lettuceLockManager;

	@Override
	public Optional<Balance> findByUserId(long userId) {
		String lockValue = lettuceLockManager.acquire(LETTUCE_LOCK_PREFIX + userId);
		try {
			return balanceJpaRepository.findByUserId(userId);
		} finally {
			lettuceLockManager.release(LETTUCE_LOCK_PREFIX + userId, lockValue);
		}
	}

}
