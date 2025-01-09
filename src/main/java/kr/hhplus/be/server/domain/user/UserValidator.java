package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserValidator {

	private final UserRepository userRepository;

	public void validate(long userId) {

		userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorType.USER_NOT_FOUND));

	}

}
