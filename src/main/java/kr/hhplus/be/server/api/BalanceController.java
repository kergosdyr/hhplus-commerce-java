package kr.hhplus.be.server.api;

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
import kr.hhplus.be.server.api.request.BalanceChargeRequest;
import kr.hhplus.be.server.api.response.BalanceChargeResponse;
import kr.hhplus.be.server.api.response.BalanceResponse;
import kr.hhplus.be.server.domain.balanace.Balance;
import kr.hhplus.be.server.domain.balanace.BalanceService;
import lombok.RequiredArgsConstructor;

@Tag(name = "Balance Management", description = "잔액 충전/조회 API")
@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
@Validated
public class BalanceController {

	private final BalanceService balanceService;

	@Operation(
		summary = "잔액 충전",
		description = "사용자 잔액을 일정 금액만큼 충전합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "충전 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping("/charge")
	public WebApiResponse<BalanceChargeResponse> charge(
		@Parameter(
			description = "잔액 충전 요청 바디", required = true
		)
		@Valid @RequestBody BalanceChargeRequest request
	) {
		var chargedBalance = balanceService.charge(request.userId(), request.amount());
		return WebApiResponse.success(BalanceChargeResponse.fromEntity(chargedBalance));
	}

	@Operation(
		summary = "잔액 조회",
		description = "사용자의 현재 잔액을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@GetMapping("/{userId}")
	public WebApiResponse<BalanceResponse> getBalance(
		@Parameter(
			name = "userId",
			description = "사용자 식별자",
			in = ParameterIn.PATH,
			required = true
		)
		@Min(value = 1, message = "userId는 1 이상의 값이어야 합니다.") @PathVariable long userId
	) {
		Balance balance = balanceService.get(userId);
		return WebApiResponse.success(BalanceResponse.fromEntity(balance));
	}
}
