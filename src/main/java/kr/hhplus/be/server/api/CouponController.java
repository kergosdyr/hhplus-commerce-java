package kr.hhplus.be.server.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.api.config.ApiResponse;
import kr.hhplus.be.server.api.request.CouponIssueRequest;
import kr.hhplus.be.server.api.response.UserCouponListResponse;
import kr.hhplus.be.server.api.response.UserCouponResponse;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

	private final CouponService couponService;

	@PostMapping("/issue")
	public ApiResponse<UserCouponResponse> issueCoupon(
		@RequestBody CouponIssueRequest request) {

		UserCoupon issuedCoupon = couponService.issue(request.userId(), request.couponId(), request.issuedAt());

		return ApiResponse.success(UserCouponResponse.fromEntity(issuedCoupon));
	}

	@GetMapping("/users/{userId}")
	public ApiResponse<UserCouponListResponse> getUserCoupons(
		@PathVariable long userId) {

		List<UserCoupon> userCoupons = couponService.load(userId);

		return ApiResponse.success(UserCouponListResponse.fromEntities(userId, userCoupons));
	}

}
