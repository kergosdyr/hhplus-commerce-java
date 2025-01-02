package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserLoader {

	private final UserRepository userRepository;

	public User load(long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorType.USER_NOT_FOUND));
	}
}
