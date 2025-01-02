package kr.hhplus.be.server.domain.balanace;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public interface BalanceRepository {

	Optional<Balance> findByUserId(long userId);
}
