package kr.hhplus.be.server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import kr.hhplus.be.server.config.WebIntegrationTest;

class CouponControllerWebIntegrationTest extends WebIntegrationTest {

	@Test
	@DisplayName("POST /api/v1/coupons/issue 로 요청 시 Mocking된 성공 응답을 반환한다")
	void apiV1PostIssueCouponSuccessTest() throws Exception {

		mockMvc.perform(
				post("/api/v1/coupons/issue")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{
								"userId": 1,
								"couponId": 2
							}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result").value("SUCCESS"))
			.andExpect(jsonPath("$.data.userId").value(1))
			.andExpect(jsonPath("$.data.userCouponId").value(111))
			.andExpect(jsonPath("$.data.couponId").value(2))
			.andExpect(jsonPath("$.data.issuedAt").value("2024-01-01T10:00:00"))
			.andExpect(jsonPath("$.data.status").value("ISSUED"));

	}

	@Test
	@DisplayName("GET /api/v1/coupons/users/{userId} 로 요청 시 Mocking된 성공 응답을 반환한다")
	void getUserCoupons() throws Exception {

		mockMvc.perform(get("/api/v1/coupons/users/{userId}", 1))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result").value("SUCCESS"))
			.andExpect(jsonPath("$.data.userId").value(1))
			.andExpect(jsonPath("$.data.coupons[0].userCouponId").value(111L))
			.andExpect(jsonPath("$.data.coupons[0].couponName").value("10% Discount"))
			.andExpect(jsonPath("$.data.coupons[0].discountValue").value(10))
			.andExpect(jsonPath("$.data.coupons[0].isPercent").value(true))
			.andExpect(jsonPath("$.data.coupons[0].expiredAt").value("2024-01-31T23:59:59"))
			.andExpect(jsonPath("$.data.coupons[1].userCouponId").value(112L))
			.andExpect(jsonPath("$.data.coupons[1].couponName").value("3000원 할인 쿠폰"))
			.andExpect(jsonPath("$.data.coupons[1].discountValue").value(3000))
			.andExpect(jsonPath("$.data.coupons[1].isPercent").value(false))
			.andExpect(jsonPath("$.data.coupons[1].expiredAt").value("2024-02-15T23:59:59"));

	}
}