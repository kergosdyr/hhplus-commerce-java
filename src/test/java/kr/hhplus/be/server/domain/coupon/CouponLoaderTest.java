package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponLoaderTest {

	@Mock
	private UserCouponRepository userCouponRepository;

	@InjectMocks
	private UserCouponLoader userCouponLoader;

	@Test
	@DisplayName("userId가 주어졌을 때, 해당 유저가 가진 모든 쿠폰을 정상적으로 조회한다.")
	void shouldLoadAllCouponsByUserId() {
		// given
		long userId = 1L;
		UserCoupon mockUserCoupon = mock(UserCoupon.class);

		when(userCouponRepository.findAllByUserId(userId))
			.thenReturn(List.of(mockUserCoupon));

		// when
		List<UserCoupon> result = userCouponLoader.loadAllByUserId(userId);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0)).isEqualTo(mockUserCoupon);
		verify(userCouponRepository, times(1)).findAllByUserId(userId);
	}

	@Test
	@DisplayName("해당 유저에게 쿠폰이 없는 경우, 빈 리스트를 반환한다.")
	void shouldReturnEmptyListWhenNoCoupons() {
		// given
		long userId = 2L;

		when(userCouponRepository.findAllByUserId(userId))
			.thenReturn(Collections.emptyList());

		// when
		List<UserCoupon> result = userCouponLoader.loadAllByUserId(userId);

		// then
		assertThat(result).isEmpty();
		verify(userCouponRepository, times(1)).findAllByUserId(userId);
	}

}
