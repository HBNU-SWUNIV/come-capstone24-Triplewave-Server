package com.hanbat.delivery.domain.request.dto;

import java.time.LocalDateTime;

import com.hanbat.delivery.domain.request.entity.Request;
import com.hanbat.delivery.domain.request.entity.RequestStatus;

public record OrderResponse(Long requestId, Long requesterId, Long receiverId, String destination,
							String stuff, String message, RequestStatus status, LocalDateTime statusTime) {
	public static OrderResponse fromRequest(Request request) {
		return new OrderResponse(request.getId(), request.getRequester().getId(),
			request.getReceiver().getId(), request.getDestination().getName(), request.getStuff(), request.getMessage(), request.getStatus(), request.getStatusTime());
	}
}
