package kr.hhplus.be.server.api.config;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.error.ErrorMessage;
import kr.hhplus.be.server.error.ErrorType;
import lombok.Getter;

public record ApiResponse<S>(@Schema(description = "응답 상태") @Getter ResultType result,
							 @Schema(description = "응답 데이터") @Getter S data,
							 @Schema(description = "에러 메세지") ErrorMessage error) {

	public static <S> ApiResponse<S> success() {
		return new ApiResponse<>(ResultType.SUCCESS, null, null);
	}

	public static <S> ApiResponse<S> success(S data) {
		return new ApiResponse<>(ResultType.SUCCESS, data, null);
	}

	public static <S> ApiResponse<S> error(ErrorType error) {
		return new ApiResponse<>(ResultType.ERROR, null, new ErrorMessage(error));
	}

	public static <S> ApiResponse<S> error(ErrorType error, Object errorData) {
		return new ApiResponse<>(ResultType.ERROR, null, new ErrorMessage(error, errorData));
	}
}
