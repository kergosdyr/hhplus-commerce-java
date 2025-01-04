package kr.hhplus.be.server.infra.storage.config.balance;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.balanace.Balance;
import kr.hhplus.be.server.domain.balanace.BalanceRepository;
@Component
public class BalanceRepositoryImpl implements BalanceRepository {
	@Override
	public Optional<Balance> findByUserId(long userId) {
		return Optional.empty();
	}
}
