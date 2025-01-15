package kr.hhplus.be.server.api;

import static kr.hhplus.be.server.config.TestUtil.createTestBalance;
import static kr.hhplus.be.server.config.TestUtil.createTestUser;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import kr.hhplus.be.server.api.request.BalanceChargeRequest;
import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.domain.balanace.Balance;
import kr.hhplus.be.server.domain.user.User;

class BalanceControllerIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("[POST] /api/v1/balance/charge - 잔액 충전 성공 테스트")
	void chargeSuccessTest() throws Exception {
		User user = createTestUser();
		userJpaRepository.save(user);
		Balance balance = createTestBalance(user.getUserId(), 50000L);
		balanceJpaRepository.save(balance);
		var request = new BalanceChargeRequest(user.getUserId(), 50000L);

		// when & then
		RestAssured
			.given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(objectMapper.writeValueAsString(request))
			.when()
			.post("/api/v1/balance/charge")
			.then()
			.statusCode(200)
			.body("data.userId", is(user.getUserId().intValue()))
			.body("data.balance", is(100000)); // 충전 후 금액 검증

	}

	@Test
	@DisplayName("[GET] /api/v1/balance/{userId} - 잔액 조회 성공 테스트")
	void getBalanceSuccessTest() {
		// given
		User user = createTestUser();
		userJpaRepository.save(user);
		Balance balance = createTestBalance(user.getUserId(), 50000L);
		balanceJpaRepository.save(balance);

		// when & then
		RestAssured
			.given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/api/v1/balance/{userId}", user.getUserId())
			.then()
			.statusCode(200)
			.body("data.userId", is(user.getUserId().intValue()))
			.body("data.balance", is(50000));

	}

}
