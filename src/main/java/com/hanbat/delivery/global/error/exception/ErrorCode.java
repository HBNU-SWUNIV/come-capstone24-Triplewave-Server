package com.hanbat.delivery.global.error.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	SAMPLE_ERROR(HttpStatus.BAD_REQUEST, "Sample Error Message"),

	// Common
	METHOD_ARGUMENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "요청한 값 타입이 잘못되어 binding에 실패하였습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지않는 HTTP method입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류, 관리자에게 문의하세요."),

	// Authentication
	TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰이 헤더에 없습니다."),
	AUTH_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "시큐리티 인증 정보를 찾을 수 없습니다."),
	TOKEN_IS_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.");

	private final HttpStatus status;
	private final String message;
}
