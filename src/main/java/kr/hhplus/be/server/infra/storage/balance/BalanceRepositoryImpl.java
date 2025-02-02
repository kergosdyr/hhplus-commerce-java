package kr.hhplus.be.server.infra.storage.balance;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.balanace.Balance;
import kr.hhplus.be.server.domain.balanace.BalanceRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile({"lettuce", "redisson"})
@Primary
public class BalanceRepositoryImpl implements BalanceRepository {

	private final BalanceJpaRepository balanceJpaRepository;

	@Override
	public Optional<Balance> findByUserId(long userId) {
		return balanceJpaRepository.findByUserId(userId);
	}

}
