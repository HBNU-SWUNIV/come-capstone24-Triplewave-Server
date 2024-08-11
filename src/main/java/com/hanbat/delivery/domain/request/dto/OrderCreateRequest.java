package com.hanbat.delivery.domain.request.dto;

import lombok.Getter;

@Getter
public class OrderCreateRequest {
	private Long requesterId;
	private Long receiverId;
	private String destination;
	private String stuff;
	private String message;



}
