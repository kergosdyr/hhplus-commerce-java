package kr.hhplus.be.server.infra.storage.balance;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.balanace.Balance;

public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {

	Optional<Balance> findByUserId(long userId);
}
