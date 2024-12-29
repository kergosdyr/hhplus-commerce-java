package kr.hhplus.be.server.presentation.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.presentation.web.config.ApiResponse;
import kr.hhplus.be.server.presentation.web.request.RechargeRequest;
import kr.hhplus.be.server.presentation.web.response.BalanceRechargeResponse;
import kr.hhplus.be.server.presentation.web.response.BalanceResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {

	@PostMapping("/recharge")
	public ApiResponse<BalanceRechargeResponse> recharge(@RequestBody RechargeRequest request) {

		return ApiResponse.success(BalanceRechargeResponse.mock(request.userId()));
	}

	@GetMapping("/{userId}")
	public ApiResponse<BalanceResponse> getBalance(@PathVariable long userId) {
		return ApiResponse.success(BalanceResponse.mock(userId));
	}

}
