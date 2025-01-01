package kr.hhplus.be.server.presentation.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.presentation.web.config.ApiResponse;
import kr.hhplus.be.server.presentation.web.request.CouponIssueRequest;
import kr.hhplus.be.server.presentation.web.response.UserCouponListResponse;
import kr.hhplus.be.server.presentation.web.response.UserCouponResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

	@PostMapping("/issue")
	public ApiResponse<UserCouponResponse> issueCoupon(@RequestBody CouponIssueRequest request) {
		return ApiResponse.success(UserCouponResponse.mock(request.userId(), request.couponId()));
	}

	@GetMapping("/users/{userId}")
	public ApiResponse<UserCouponListResponse> getUserCoupons(@PathVariable long userId) {
		return ApiResponse.success(UserCouponListResponse.mock(userId));
	}

}