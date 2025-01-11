package kr.hhplus.be.server.api;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import kr.hhplus.be.server.api.config.WebApiResponse;
import kr.hhplus.be.server.api.request.CouponIssueRequest;
import kr.hhplus.be.server.api.response.UserCouponListResponse;
import kr.hhplus.be.server.api.response.UserCouponResponse;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import lombok.RequiredArgsConstructor;

@Tag(name = "Coupon", description = "선착순 쿠폰 발급/조회 API")
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@Validated
public class CouponController {

	private final CouponService couponService;

	@Operation(
		summary = "쿠폰 발급",
		description = "선착순 쿠폰을 발급. 쿠폰 재고가 소진되면 발급이 불가합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "발급 성공"),
		@ApiResponse(responseCode = "400", description = "이미 발급됨 or 재고 소진"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping("/issue")
	public WebApiResponse<UserCouponResponse> issueCoupon(
		@Parameter(
			description = "쿠폰 발급 요청 바디", required = true
		)
		@Valid @RequestBody CouponIssueRequest request
	) {
		UserCoupon issuedCoupon = couponService.issue(request.userId(), request.couponId(), request.issuedAt());
		return WebApiResponse.success(UserCouponResponse.fromEntity(issuedCoupon));
	}

	@Operation(
		summary = "사용자 보유 쿠폰 조회",
		description = "해당 유저가 현재 보유 중인 모든 쿠폰을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "존재하지 않는 사용자"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@GetMapping("/users/{userId}")
	public WebApiResponse<UserCouponListResponse> getUserCoupons(
		@Parameter(
			name = "userId",
			description = "사용자 식별자",
			in = ParameterIn.PATH,
			required = true
		)
		@Min(value = 1, message = "userId는 1 이상의 값이어야 합니다.") @PathVariable long userId
	) {
		List<UserCoupon> userCoupons = couponService.load(userId);
		return WebApiResponse.success(UserCouponListResponse.fromEntities(userId, userCoupons));
	}
}
