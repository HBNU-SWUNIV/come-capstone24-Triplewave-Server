package com.hanbat.delivery.domain.request.dto;

import java.time.LocalDateTime;

import com.hanbat.delivery.domain.request.entity.Request;
import com.hanbat.delivery.domain.request.entity.RequestStatus;

public record OrderAcceptedResponse(Long requestId, Long requesterId, Long receiverId, String destination,
									String departure, String stuff, String message, RequestStatus status, LocalDateTime statusTime) {
	public static OrderAcceptedResponse fromRequest(Request request) {
		return new OrderAcceptedResponse(request.getId(), request.getRequester().getId(),
			request.getReceiver().getId(), request.getDestination().getName(), request.getDeparture().getName(), request.getStuff(), request.getMessage(), request.getStatus(), request.getStatusTime());

	}
}
