package com.hanbat.delivery.domain.request.dto;

import lombok.Getter;

@Getter
public class OrderAcceptedRequest {
	private Long requestId;
	private String departure;
}
