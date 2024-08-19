package com.hanbat.delivery.global.websocket.dto;

import java.util.List;

import org.json.simple.JSONObject;

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
	private Double resolution;
	private JSONObject origin;
	private List<Integer> data;

	@Builder
	public MapResponse(Long width, Long height, JSONObject origin, Double resolution, List<Integer> data) {
		this.width = width;
		this.height = height;
		this.origin = origin;
		this.resolution = resolution;
		this.data = data;
	}
}
