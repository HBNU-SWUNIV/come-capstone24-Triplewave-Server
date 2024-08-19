package com.hanbat.delivery.global.websocket.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class MapResponse {
	private Long width;
	private Long height;
	private List<Integer> data;

	@Builder
	public MapResponse(Long width, Long height, List<Integer> data) {
		this.width = width;
		this.height = height;
		this.data = data;
	}
}
