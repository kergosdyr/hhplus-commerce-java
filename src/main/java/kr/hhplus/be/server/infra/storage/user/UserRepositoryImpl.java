package kr.hhplus.be.server.infra.storage.user;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	@Override
	public Optional<User> findById(long userId) {
		return userJpaRepository.findById(userId);
	}
}