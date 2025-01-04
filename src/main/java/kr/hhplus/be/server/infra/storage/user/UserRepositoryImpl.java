package kr.hhplus.be.server.infra.storage.user;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;

@Component
public class UserRepositoryImpl implements UserRepository {
	@Override
	public Optional<User> findById(long userId) {
		return Optional.empty();
	}
}