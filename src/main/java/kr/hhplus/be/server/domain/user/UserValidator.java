package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserValidator {

	private final UserRepository userRepository;

	public void validate(long userId) {

		userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorType.USER_NOT_FOUND));

	}

}
