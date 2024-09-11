package com.hanbat.delivery.domain.request.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanbat.delivery.domain.request.application.RequestService;
import com.hanbat.delivery.domain.request.dto.OrderAcceptedRequest;
import com.hanbat.delivery.domain.request.dto.OrderAcceptedResponse;
import com.hanbat.delivery.domain.request.dto.OrderCreateRequest;
import com.hanbat.delivery.domain.request.dto.OrderResponse;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/request")
@Slf4j
public class RequestController {
	private final RequestService requestService;

	@PostMapping("/create")
	public ResponseEntity<OrderResponse> createRequest(@RequestBody @Valid OrderCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequest(request));
	}

	@GetMapping("/get/{requestId}")
	public ResponseEntity<OrderResponse> getRequest(@PathVariable("requestId") Long requestId) {
		return ResponseEntity.ok(requestService.getRequest(requestId));
	}

	@PostMapping("/accept")
	public ResponseEntity<OrderAcceptedResponse> acceptRequest(@RequestBody @Valid OrderAcceptedRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(requestService.acceptRequest(request));
	}

	@PostMapping("/complete/{requestId}")
	public ResponseEntity<OrderResponse> completeRequest(@PathVariable("requestId") Long requestId) {
		return ResponseEntity.status(HttpStatus.OK).body(requestService.confirmIfDelivered(requestId));
	}

	@GetMapping("/completed/get")
	public ResponseEntity<List<OrderResponse>> getCompletedRequests(@RequestParam(name = "dateRange") String dateRange) {
		return ResponseEntity.ok(requestService.getCompletedRequestsByDateRange(dateRange));
	}

}
