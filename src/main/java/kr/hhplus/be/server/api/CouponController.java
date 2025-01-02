package kr.hhplus.be.server.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.api.config.ApiResponse;
import kr.hhplus.be.server.api.request.CouponIssueRequest;
import kr.hhplus.be.server.api.response.UserCouponListResponse;
import kr.hhplus.be.server.api.response.UserCouponResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

	@PostMapping("/issue")
	public ApiResponse<UserCouponResponse> issueCoupon(@RequestHeader("Authorization") String authHeader,
		@RequestBody CouponIssueRequest request) {
		return ApiResponse.success(UserCouponResponse.mock(request.userId(), request.couponId()));
	}

	@GetMapping("/users/{userId}")
	public ApiResponse<UserCouponListResponse> getUserCoupons(@RequestHeader("Authorization") String authHeader,
		@PathVariable long userId) {
		return ApiResponse.success(UserCouponListResponse.mock(userId));
	}

}
