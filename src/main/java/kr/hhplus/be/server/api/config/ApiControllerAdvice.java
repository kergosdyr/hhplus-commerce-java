package kr.hhplus.be.server.api.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ApiControllerAdvice {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<WebApiResponse<?>> handleCoreApiException(ApiException e) {
		switch (e.getErrorType().getLogLevel()) {
			case ERROR -> log.error("ApiException : {}", e.getMessage(), e);
			case WARN -> log.warn("ApiException : {}", e.getMessage(), e);
			default -> log.info("ApiException : {}", e.getMessage(), e);
		}
		return new ResponseEntity<>(
			WebApiResponse.error(e.getErrorType(), e.getData()),
			e.getErrorType().getStatus());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<WebApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.warn("Validation Error: {}", e.getMessage());

		// FieldError 목록 추출
		List<String> errorMessages = e.getBindingResult().getAllErrors().stream()
			.map(error -> {
				if (error instanceof FieldError fieldError) {
					return String.format("[%s] %s", fieldError.getField(), fieldError.getDefaultMessage());
				}
				return error.getDefaultMessage();
			})
			.toList();

		var errorType = ErrorType.BAD_REQUEST_ERROR; // 예: 400
		return new ResponseEntity<>(
			WebApiResponse.error(errorType, String.join(", ", errorMessages)),
			errorType.getStatus()
		);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<WebApiResponse<?>> handleConstraintViolationException(ConstraintViolationException e) {
		log.warn("ConstraintViolation Error: {}", e.getMessage());

		// PathVariable, RequestParam 검증 실패 시 에러 목록
		List<String> errorMessages = e.getConstraintViolations().stream()
			.map(ConstraintViolation::getMessage)
			.collect(Collectors.toList());

		var errorType = ErrorType.BAD_REQUEST_ERROR;
		return new ResponseEntity<>(
			WebApiResponse.error(errorType, String.join(", ", errorMessages)),
			errorType.getStatus()
		);
	}


	@ExceptionHandler(Exception.class)
	public ResponseEntity<WebApiResponse<?>> handleException(Exception e) {
		log.error("Exception : {}", e.getMessage(), e);
		return new ResponseEntity<>(WebApiResponse.error(ErrorType.DEFAULT_ERROR), ErrorType.DEFAULT_ERROR.getStatus());
	}
}
