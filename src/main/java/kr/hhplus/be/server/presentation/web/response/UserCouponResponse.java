package kr.hhplus.be.server.presentation.web.response;

public record UserCouponResponse(Long userCouponId, Long userId, Long couponId,
								 String issuedAt, String status) {
	public static UserCouponResponse mock(long userId, long couponId) {
		return new UserCouponResponse(
			111L,
			userId,
			couponId,
			"2024-01-01T10:00:00",
			"ISSUED"
		);
	}
}
