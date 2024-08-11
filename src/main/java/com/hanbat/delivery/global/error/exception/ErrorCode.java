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
	TOKEN_IS_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

	// Member
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "멤버를 찾을 수 없습니다."),

	// Request
	REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "요청을 찾을 수 없습니다."),
	RECEIVER_IS_NOT_RIGHT(HttpStatus.BAD_REQUEST, "해당 주문의 수신자가 아닙니다."),

	// Location
	LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "위치를 찾을 수 없습니다."),

	// Rosbridge
	ROSBRIDGE_NOT_CONNECTED(HttpStatus.INTERNAL_SERVER_ERROR, "Rosbridge 연결이 실패했습니다.");

	private final HttpStatus status;
	private final String message;
}
