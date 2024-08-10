package com.hanbat.delivery.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Major {
	Computer("컴퓨터공학과"),
	Industrial("산업경영공학과");


	@JsonValue
	private final String value;

}
