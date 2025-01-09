package kr.hhplus.be.server.error;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorType {
	DEFAULT_ERROR(
		HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "예상치 못한 오류가 발생하였습니다. 잠시 후 다시 시도해주세요.", LogLevel.ERROR),
	USER_NOT_FOUND(
		HttpStatus.NOT_FOUND, ErrorCode.E400, "요청하신 유저를 찾을 수 없습니다", LogLevel.ERROR),
	BALANCE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500,
		"잔액 충전 처리 중 오류가 발생했습니다.", LogLevel.ERROR), COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E400,
		"요청하신 쿠폰을 찾을 수 없습니다", LogLevel.ERROR),
	COUPON_ALREADY_ISSUED(HttpStatus.CONFLICT, ErrorCode.E400, "쿠폰은 중복해서 발급할 수 없습니다",
		LogLevel.ERROR), COUPON_NOT_ISSUABLE(
		HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "쿠폰이 만료되었거나 모두 소진되었습니다.", LogLevel.ERROR),

	BALANCE_OVER_USE(HttpStatus.NOT_ACCEPTABLE, ErrorCode.E400, "잔액을 초과해서 사용할 수 없습니다", LogLevel.ERROR),
	BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, ErrorCode.E400, "잘못된 요청 정보를 전송하셨습니다.", LogLevel.ERROR);

	private final HttpStatus status;

	private final ErrorCode code;

	private final String message;

	private final LogLevel logLevel;

	ErrorType(HttpStatus status, ErrorCode code, String message, LogLevel logLevel) {

		this.status = status;
		this.code = code;
		this.message = message;
		this.logLevel = logLevel;
	}
}
