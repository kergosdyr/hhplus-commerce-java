package kr.hhplus.be.server.infra.storage.balance;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.balanace.Balance;

public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {

	@Query("select b from Balance b where b.userId = ?1")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Balance> findByUserIdWithLock(long userId);
}
