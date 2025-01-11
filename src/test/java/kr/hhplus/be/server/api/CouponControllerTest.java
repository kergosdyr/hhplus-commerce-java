package kr.hhplus.be.server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;

import kr.hhplus.be.server.api.request.CouponIssueRequest;
import kr.hhplus.be.server.config.WebIntegrationTest;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.enums.UserCouponStatus;

class CouponControllerTest extends WebIntegrationTest {

	@Test
	@DisplayName("[POST] /api/v1/coupons/issue - 쿠폰 발급 성공 테스트")
	void issueCouponSuccessTest() throws Exception {
		// given
		var request = new CouponIssueRequest(123L, 999L, LocalDateTime.now());

		var mockUserCoupon = UserCoupon.builder()
			.userCouponId(1L)
			.userId(123L)
			.couponId(999L)
			.amount(500L)
			.status(UserCouponStatus.AVAILABLE)
			.issuedAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0, 0))
			.expiredAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0, 0))
			.isUsed(false)
			.build();

		Mockito.when(couponService.issue(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
			.thenReturn(mockUserCoupon);

		// when & then
		mockMvc.perform(
				post("/api/v1/coupons/issue")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userCouponId").value(1L))
			.andExpect(jsonPath("$.data.userId").value(123L))
			.andExpect(jsonPath("$.data.couponId").value(999L))
			.andExpect(jsonPath("$.data.issuedAt").exists());
	}

	@Test
	@DisplayName("[GET] /api/v1/coupons/users/{userId} - 보유 쿠폰 조회 성공 테스트")
	void getUserCouponsSuccessTest() throws Exception {
		// given
		var mockUserCoupons = List.of(
			UserCoupon.builder()
				.userCouponId(1L)
				.userId(123L)
				.couponId(999L)
				.amount(500L)
				.status(UserCouponStatus.AVAILABLE)
				.issuedAt(LocalDateTime.now())
				.expiredAt(LocalDateTime.now().plusDays(30))
				.isUsed(false)
				.coupon(Coupon.builder()
					.couponId(999L)
					.name("Discount Coupon")
					.amount(500L)
					.build())
				.build(),
			UserCoupon.builder()
				.userCouponId(2L)
				.userId(123L)
				.couponId(1000L)
				.amount(300L)
				.status(UserCouponStatus.USED)
				.issuedAt(LocalDateTime.now().minusDays(10))
				.expiredAt(LocalDateTime.now().plusDays(20))
				.isUsed(true)
				.coupon(Coupon.builder()
					.couponId(1000L)
					.name("Gift Coupon")
					.amount(300L)
					.build())
				.build()
		);

		Mockito.when(couponService.load(123L)).thenReturn(mockUserCoupons);

		// when & then
		mockMvc.perform(get("/api/v1/coupons/users/123"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userId").value(123L))
			.andExpect(jsonPath("$.data.coupons.length()").value(2))
			.andExpect(jsonPath("$.data.coupons[0].userCouponId").value(1L))
			.andExpect(jsonPath("$.data.coupons[0].couponId").value(999L))
			.andExpect(jsonPath("$.data.coupons[0].couponName").value("Discount Coupon"))
			.andExpect(jsonPath("$.data.coupons[0].amount").value(500L))
			.andExpect(jsonPath("$.data.coupons[0].isUsed").value(false))
			.andExpect(jsonPath("$.data.coupons[0].expiredAt").exists())
			.andExpect(jsonPath("$.data.coupons[1].userCouponId").value(2L))
			.andExpect(jsonPath("$.data.coupons[1].couponId").value(1000L))
			.andExpect(jsonPath("$.data.coupons[1].couponName").value("Gift Coupon"))
			.andExpect(jsonPath("$.data.coupons[1].amount").value(300L))
			.andExpect(jsonPath("$.data.coupons[1].isUsed").value(true))
			.andExpect(jsonPath("$.data.coupons[1].expiredAt").exists());
	}

	@Test
	@DisplayName("[POST] /api/v1/coupons/issue - userId가 0인 경우 Validation 실패")
	void issueCouponWithInvalidUserId() throws Exception {
		// given
		// userId=0 => @Min(1) 위배
		var invalidRequest = new CouponIssueRequest(0L, 999L, LocalDateTime.now());

		// when & then
		mockMvc.perform(
				post("/api/v1/coupons/issue")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(invalidRequest))
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.code").value("E400"))  // 예시
			.andExpect(jsonPath("$.error.message").value(org.hamcrest.Matchers.containsString("잘못된 요청 정보를 전송하셨습니다")))
			.andExpect(jsonPath("$.error.data").value(org.hamcrest.Matchers.containsString("userId는 1 이상의 값")));
	}

	@Test
	@DisplayName("[POST] /api/v1/coupons/issue - couponId가 0인 경우 Validation 실패")
	void issueCouponWithInvalidCouponId() throws Exception {
		// given
		// couponId=0 => @Min(1) 위배
		var invalidRequest = new CouponIssueRequest(123L, 0L, LocalDateTime.now());

		// when & then
		mockMvc.perform(
				post("/api/v1/coupons/issue")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(invalidRequest))
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.code").value("E400"))
			.andExpect(jsonPath("$.error.message").value(org.hamcrest.Matchers.containsString("잘못된 요청 정보를 전송하셨습니다")))
			.andExpect(jsonPath("$.error.data").value(org.hamcrest.Matchers.containsString("couponId는 1 이상의 값")));
	}

	@Test
	@DisplayName("[POST] /api/v1/coupons/issue - issuedAt이 null인 경우 Validation 실패")
	void issueCouponWithNullIssuedAt() throws Exception {
		// given
		// issuedAt=null => @NotNull 위배
		var invalidRequest = new CouponIssueRequest(123L, 999L, null);

		// when & then
		mockMvc.perform(
				post("/api/v1/coupons/issue")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(invalidRequest))
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.code").value("E400"))
			.andExpect(jsonPath("$.error.message").value(org.hamcrest.Matchers.containsString("잘못된 요청 정보를 전송하셨습니다")))
			.andExpect(jsonPath("$.error.data").value(org.hamcrest.Matchers.containsString("issuedAt은 필수값")));
	}

}
