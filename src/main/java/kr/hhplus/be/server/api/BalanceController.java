package kr.hhplus.be.server.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.api.config.ApiResponse;
import kr.hhplus.be.server.api.request.BalanceChargeRequest;
import kr.hhplus.be.server.api.response.BalanceChargeResponse;
import kr.hhplus.be.server.api.response.BalanceResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {


	@PostMapping("/charge")
	public ApiResponse<BalanceChargeResponse> charge(
		@RequestHeader("Authorization") String authHeader,
		@RequestBody BalanceChargeRequest request) {
		return ApiResponse.success(BalanceChargeResponse.mock(request.userId()));
	}

	@GetMapping("/{userId}")
	public ApiResponse<BalanceResponse> getBalance(@RequestHeader("Authorization") String authHeader,
		@PathVariable long userId) {
		return ApiResponse.success(BalanceResponse.mock(userId));
	}

}
