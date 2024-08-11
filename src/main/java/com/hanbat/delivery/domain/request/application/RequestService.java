package com.hanbat.delivery.domain.request.application;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.hanbat.delivery.domain.location.entity.Location;
import com.hanbat.delivery.domain.location.repository.LocationRepository;
import com.hanbat.delivery.domain.member.entity.Member;
import com.hanbat.delivery.domain.member.repository.MemberRepository;
import com.hanbat.delivery.domain.request.dto.OrderAcceptedRequest;
import com.hanbat.delivery.domain.request.dto.OrderAcceptedResponse;
import com.hanbat.delivery.domain.request.dto.OrderCreateRequest;
import com.hanbat.delivery.domain.request.dto.OrderResponse;
import com.hanbat.delivery.domain.request.entity.Request;
import com.hanbat.delivery.domain.request.entity.RequestStatus;
import com.hanbat.delivery.domain.request.repository.RequestRepository;
import com.hanbat.delivery.global.error.exception.CustomException;
import com.hanbat.delivery.global.error.exception.ErrorCode;
import com.hanbat.delivery.global.websocket.handler.RosWebSocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

	private final RequestRepository requestRepository;
	private final MemberRepository memberRepository;
	private final LocationRepository locationRepository;

	@Value("${ros.api.uri}")
	private String rosBridgeApiUrl;

	// 주문서 생성
	@Transactional
	public OrderResponse createRequest(OrderCreateRequest orderCreateRequest) {
		Member requester = memberRepository.findById(orderCreateRequest.getRequesterId())
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		Member receiver = memberRepository.findById(orderCreateRequest.getReceiverId())
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		Location location = locationRepository.findByName(orderCreateRequest.getDestination())
			.orElseThrow(() -> new CustomException(ErrorCode.LOCATION_NOT_FOUND));

		Request request = Request.builder()
			.requester(requester)
			.receiver(receiver)
			.stuff(orderCreateRequest.getStuff())
			.destination(location)
			.message(orderCreateRequest.getMessage())
			.status(RequestStatus.PENDING)
			.statusTime(LocalDateTime.now())
			.build();

		requestRepository.save(request);
		return OrderResponse.fromRequest(request);

	}

	// 주문서 조회
	public OrderResponse getRequest(Long requestId) {

		Long userId = 2L;

		Request request = requestRepository.findById(requestId)
			.orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));

		// 현재 사용자가 주문서의 수신자가 아니고, 현재 사용자가 주문서의 발송자도 아닌 경우, 에러 발생
		if (!request.getReceiver().getId().equals(userId) && !request.getRequester().getId().equals(userId)) {
			throw new CustomException(ErrorCode.RECEIVER_IS_NOT_RIGHT);
		}

		return OrderResponse.fromRequest(request);
	}

}
