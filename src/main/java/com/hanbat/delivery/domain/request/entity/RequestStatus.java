package com.hanbat.delivery.domain.request.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestStatus {
	PENDING, ACCEPTED, IN_PROGRESS, DELIVERED, COMPLETED, CANCELED
}
