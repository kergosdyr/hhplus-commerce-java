package kr.hhplus.be.server.domain.user;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public interface UserRepository {

	Optional<User> findById(long userId);
}
