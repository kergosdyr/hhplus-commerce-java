package kr.hhplus.be.server.api;

import static kr.hhplus.be.server.config.TestUtil.createTestCoupon;
import static kr.hhplus.be.server.config.TestUtil.createTestCouponInventory;
import static kr.hhplus.be.server.config.TestUtil.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import kr.hhplus.be.server.api.request.CouponIssueRequest;
import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponInventory;
import kr.hhplus.be.server.domain.user.User;

class CouponControllerIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("[POST] /api/v1/coupons/issue - 쿠폰 발급 성공 테스트")
	void issueCouponSuccessTest() throws Exception {

		User user = createTestUser();
		userJpaRepository.save(user);
		Coupon coupon = createTestCoupon(LocalDateTime.now().plusDays(1L));
		Coupon savedCoupon = couponJpaRepository.save(coupon);
		CouponInventory couponInventory = createTestCouponInventory(savedCoupon.getCouponId(), 30L);
		couponInventoryJpaRepository.save(couponInventory);

		// given
		var request = new CouponIssueRequest(user.getUserId(), coupon.getCouponId(), LocalDateTime.now());

		// when
		var response = RestAssured
			.given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(objectMapper.writeValueAsString(request))
			.when()
			.post("/api/v1/coupons/issue")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.extract()
			.jsonPath();

		// then
		assertThat(response.getLong("data.userCouponId")).isEqualTo(1L);
		assertThat(response.getLong("data.userId")).isEqualTo(user.getUserId());
		assertThat(response.getLong("data.couponId")).isEqualTo(coupon.getCouponId());
		assertThat(response.getString("data.status")).isEqualTo("AVAILABLE");
		assertThat(response.getString("data.issuedAt")).isNotNull();

	}

}
