package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.config.TestUtil;

@ExtendWith(MockitoExtension.class)
class CouponIssuerTest {

	@Mock
	private UserCouponRepository userCouponRepository;

	@Mock
	private UserCouponValidator userCouponValidator;

	@Mock
	private CouponReader couponReader;

	@Mock
	private CouponPublisher couponPublisher;

	@InjectMocks
	private CouponIssuer couponIssuer;

	@Test
	@DisplayName("CouponIssuer 의 issue 를 호출하면, 쿠폰 발급 대기열에 등록을 호출하고 UserCoupon 을 반환한다.")
	void shouldCreateUserCouponAndSaveWhenCouponIssuerIssueCalled() {
		//given
		long userId = 123L;
		long couponId = 1L;
		long quantity = 10L;

		doNothing().when(userCouponValidator).validate(userId, couponId);
		Coupon testCouponWithInventory = TestUtil.createTestCouponWithInventory(LocalDateTime.of(2025, 12, 31, 0, 0));

		LocalDateTime issuedAt = LocalDateTime.of(2025, 1, 1, 0, 0);

		when(couponReader.readIssuable(couponId, issuedAt)).thenReturn(testCouponWithInventory);

		when(couponPublisher.publishCouponIssueWait(userId, couponId, issuedAt)).thenReturn(true);
		//when
		UserCoupon issuedUserCoupon = couponIssuer.issue(userId, couponId, issuedAt);

		//then
		verify(couponPublisher, times(1)).publishCouponIssueWait(userId, couponId, issuedAt);
		assertThat(issuedUserCoupon.getCouponId()).isEqualTo(testCouponWithInventory.getCouponId());
		assertThat(issuedUserCoupon.getUserId()).isEqualTo(userId);
		assertThat(issuedUserCoupon.getIssuedAt()).isEqualTo(issuedAt);



	}

}
