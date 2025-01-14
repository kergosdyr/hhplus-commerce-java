package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFinder {

	private final UserRepository userRepository;

	public User findByUserId(long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorType.USER_NOT_FOUND));
	}

	public boolean notExistsByUserId(long userId) {
		return !userRepository.existsById(userId);
	}
}
