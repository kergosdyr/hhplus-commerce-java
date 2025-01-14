package kr.hhplus.be.server.domain.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.error.ApiException;

@ExtendWith(MockitoExtension.class)
class UserFinderTest {

	@Mock
	UserRepository userRepository;

	@InjectMocks
	UserFinder userFinder;

	@Test
	@DisplayName("유저가 존재하지 않으면, ApiException을 발생시키고 User를 찾을 수 없다.")
	void shouldUserNotExistThrowApiException() {

		// given
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

		// when
		// then
		assertThatThrownBy(() -> {
			userFinder.findByUserId(1L);
		}).isInstanceOf(ApiException.class).hasMessageContaining("요청하신 유저를 찾을 수 없습니다");

	}

	@Test
	@DisplayName("유저가 존재하면, User를 반환한다.")
	void shouldUserExistReturnUser() {

		// given
		User user = User.builder().userId(1L).build();
		Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		// when
		User loadedUser = userFinder.findByUserId(1L);

		// then
		Assertions.assertThat(user).isEqualTo(loadedUser);

	}

}