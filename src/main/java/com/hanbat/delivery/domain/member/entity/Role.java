package com.hanbat.delivery.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
	PROFESSOR("교수"),
	STUDENT("학생"),
	ASSISTANT("조교");

	@JsonValue
	private final String value;
}
