package kr.hhplus.be.server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import kr.hhplus.be.server.config.WebIntegrationTest;
import kr.hhplus.be.server.util.MockJwt;

class BalanceControllerWebIntegrationTest extends WebIntegrationTest {

	@Test
	@DisplayName("POST /api/v1/balance/charge 로 요청 시 Mocking된 성공 응답을 반환한다")
	void apiV1BalanceRechargeSuccessTest() throws Exception {

		mockMvc.perform(
				post("/api/v1/balance/charge")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{
								"userId": 1,
								"amount": 10000
							}
						""").header("Authorization", "Bearer " + MockJwt.MOCK.getToken()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result").value("SUCCESS"))
			.andExpect(jsonPath("$.data.userId").value(1))
			.andExpect(jsonPath("$.data.balance").value(150000L));
	}

	@Test
	@DisplayName("GET /api/v1/balance/{userId} 로 요청 시 Mocking된 성공 응답을 반환한다")
	void apiV1GetBalanceUserIdSuccessTest() throws Exception {

		mockMvc.perform(
				get("/api/v1/balance/{userId}", 1).header("Authorization", "Bearer " + MockJwt.MOCK.getToken()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result").value("SUCCESS"))
			.andExpect(jsonPath("$.data.userId").value(1))
			.andExpect(jsonPath("$.data.balance").value(150000L));
	}

}