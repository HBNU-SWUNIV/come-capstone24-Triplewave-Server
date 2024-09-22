package com.hanbat.delivery.global.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.hanbat.delivery.global.common.response.GlobalResponse;
import com.hanbat.delivery.global.error.exception.CustomException;
import com.hanbat.delivery.global.error.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice // 전역 예외 처리
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/** handleExceptionInternal() 메소드를 오버라이딩해 응답 커스터마이징 **/
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
		HttpStatusCode statusCode, WebRequest request) {
		ErrorResponse errorResponse =
			ErrorResponse.of(ex.getClass().getSimpleName(), ex.getMessage());
		return super.handleExceptionInternal(ex, errorResponse, headers, statusCode, request);
	}

	/**
	 * javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다. HttpMessageConverter 에서 등록한
	 * HttpMessageConverter binding 못할경우 발생 주로 @RequestBody, @RequestPart 어노테이션에서 발생
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
		HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		log.error("MethodArgumentNotValidException : {}", e.getMessage(), e);
		String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
		final ErrorResponse errorResponse =
			ErrorResponse.of(e.getClass().getSimpleName(), errorMessage);
		GlobalResponse response = GlobalResponse.fail(status.value(), errorResponse);
		return ResponseEntity.status(status).body(response);
	}

	/** PathVariable, RequestParam, RequestHeader, RequestBody에서 타입이 일치하지않을 경우 발생 **/
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<GlobalResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		log.error("MethodArgumentTypeMismatchException : {}", e.getMessage(), e);
		final ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH;
		final ErrorResponse errorResponse =
			ErrorResponse.of(e.getClass().getSimpleName(), errorCode.getMessage());
		final GlobalResponse response =
			GlobalResponse.fail(errorCode.getStatus().value(), errorResponse);
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}


	/** CustomException 예외 처리 **/
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<GlobalResponse> handleCustomException(CustomException e){
		log.error("CustomeException : {}", e.getMessage(), e);
		final ErrorCode errorCode = e.getErrorCode();
		final ErrorResponse errorResponse =
			ErrorResponse.of(errorCode.name(), errorCode.getMessage());
		final GlobalResponse response =
			GlobalResponse.fail(errorCode.getStatus().value(), errorResponse);
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	/** 500번대 에러 처리**/
	@ExceptionHandler(Exception.class)
	protected ResponseEntity<GlobalResponse> handleException(Exception e) {
		log.error("Internal Server Error : {}", e.getMessage(), e);
		final ErrorCode internalServerError = ErrorCode.INTERNAL_SERVER_ERROR;
		final ErrorResponse errorResponse =
			ErrorResponse.of(e.getClass().getSimpleName(), internalServerError.getMessage());
		final GlobalResponse response =
			GlobalResponse.fail(internalServerError.getStatus().value(), errorResponse);
		return ResponseEntity.status(internalServerError.getStatus()).body(response);
	}

	/** 지원하지 않은 HTTP method 호출 할 경우 발생 **/
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
		HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		log.error("HttpRequestMethodNotSupportedException : {}", e.getMessage(), e);
		final ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
		final ErrorResponse errorResponse =
			ErrorResponse.of(e.getClass().getSimpleName(), errorCode.getMessage());
		final GlobalResponse response =
			GlobalResponse.fail(errorCode.getStatus().value(), errorResponse);
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}
}
