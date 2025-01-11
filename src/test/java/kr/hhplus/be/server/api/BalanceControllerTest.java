package kr.hhplus.be.server.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import kr.hhplus.be.server.api.request.BalanceChargeRequest;
import kr.hhplus.be.server.config.WebIntegrationTest;
import kr.hhplus.be.server.domain.balanace.Balance;

class BalanceControllerTest extends WebIntegrationTest {

	@Test
	@DisplayName("[POST] /api/v1/balance/charge - 잔액 충전 성공 테스트")
	void chargeSuccessTest() throws Exception {
		// given
		var request = new BalanceChargeRequest(123L, 50000L);
		var mockBalance = Balance.builder()
			.userId(123L)
			.amount(50000L)
			.build();

		// when
		Mockito.when(balanceService.charge(123L, 50000L)).thenReturn(mockBalance);

		// then
		mockMvc.perform(
				post("/api/v1/balance/charge")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userId").value(123))
			.andExpect(jsonPath("$.data.balance").value(50000));
	}

	@Test
	@DisplayName("[GET] /api/v1/balance/{userId} - 잔액 조회 성공 테스트")
	void getBalanceSuccessTest() throws Exception {
		// given
		var mockBalance = Balance.builder()
			.userId(123L)
			.amount(150000L)
			.build();
		Mockito.when(balanceService.get(123L)).thenReturn(mockBalance);

		// when & then
		mockMvc.perform(get("/api/v1/balance/123"))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.userId").value(123))
			.andExpect(jsonPath("$.data.balance").value(150000));
	}

	@Test
	@DisplayName("[POST] /api/v1/balance/charge - 유효성 검증 실패(충전 금액 0원)")
	void chargeFailBecauseOfInvalidAmount() throws Exception {
		// given
		var invalidRequest = new BalanceChargeRequest(123L, 0L); // 0원 -> @Min(100) 위배

		// when & then
		mockMvc.perform(
				post("/api/v1/balance/charge")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(invalidRequest))
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isBadRequest()) // 400
			.andExpect(jsonPath("$.error.code").value("E400"))
			.andExpect(jsonPath("$.error.message").value(org.hamcrest.Matchers.containsString("잘못된 요청 정보를 전송하셨습니다.")))
			.andExpect(jsonPath("$.error.data").value(org.hamcrest.Matchers.containsString("최소 100원 이상 충전 가능합니다.")));

	}

	@Test
	@DisplayName("[POST] /api/v1/balance/charge - 유효성 검증 실패(userId = 0)")
	void chargeFailBecauseOfInvalidUserId() throws Exception {
		// given
		var invalidRequest = new BalanceChargeRequest(0L, 1000L);

		mockMvc.perform(
				post("/api/v1/balance/charge")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(invalidRequest))
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error.code").value("E400"))
			.andExpect(jsonPath("$.error.message").value(org.hamcrest.Matchers.containsString("잘못된 요청 정보를 전송하셨습니다")))
			.andExpect(jsonPath("$.error.data").value(org.hamcrest.Matchers.containsString("userId는 1 이상의 값이어야 합니다.")));

	}

}
