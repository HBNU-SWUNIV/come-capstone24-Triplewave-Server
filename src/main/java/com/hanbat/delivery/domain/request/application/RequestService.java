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
import com.hanbat.delivery.global.sse.SseEmitters;
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
	private final SseEmitters sseEmitters;

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


	// 주문 수락
	@Transactional
	public OrderAcceptedResponse acceptRequest(OrderAcceptedRequest orderAcceptedRequest) {

		Long userId = 2L;

		Request request = requestRepository.findById(orderAcceptedRequest.getRequestId())
			.orElseThrow(() -> new CustomException(ErrorCode.REQUEST_NOT_FOUND));

		// 현재 사용자가 주문서의 수신자가 아니고, 현재 사용자가 주문서의 발송자도 아닌 경우, 에러 발생
		if (!request.getReceiver().getId().equals(userId) && !request.getRequester().getId().equals(userId)) {
			throw new CustomException(ErrorCode.RECEIVER_IS_NOT_RIGHT);
		}

		Location location = locationRepository.findByName(orderAcceptedRequest.getDeparture())
				.orElseThrow(() -> new CustomException(ErrorCode.LOCATION_NOT_FOUND));

		request.updateDeparture(location);
		request.updateAcceptedStatus();

		// 로봇에게 네비게이션 명령 전달
		try {
			WebSocketClient client = new StandardWebSocketClient();
			WebSocketSession session = client.execute(new RosWebSocketHandler(sseEmitters, this), rosBridgeApiUrl).get();

			// 맵을 띄우기위해 /map 토픽 구독 -> 데이터가 너무 많아 에러 발생
			// ros에서 파싱해서 rosbridge에 데이터 전달, 서버에서 해당 토픽 발행 후 구독
			WebSocketMessage<String> advertiseMapMessage = advertiseMapTopic();
			session.sendMessage(advertiseMapMessage);
			WebSocketMessage<String> mapMessage = subscribeMapTopic();
			session.sendMessage(mapMessage);

			WebSocketMessage<String> navigationMessage = createRobotNavigationMessage(location);
			session.sendMessage(navigationMessage);

			request.updateInProgressStatus();

			// 네비게이션 로봇 위치 파악 토픽 구독
			WebSocketMessage<String> odomMessage = subscribeAmclTopic();
			session.sendMessage(odomMessage);

			// 네비게이션 로봇 목적지 도착 상태파악 토픽 구독
			WebSocketMessage<String> statusMessage = subscribeMoveBaseStatusTopic();
			session.sendMessage(statusMessage);

			// goal id를 얻기위해 move_base/goal 구독
			WebSocketMessage<String> goalIdMessage = subscribeMoveBaseGoalTopic();
			session.sendMessage(goalIdMessage);

			return OrderAcceptedResponse.fromRequest(request);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new CustomException(ErrorCode.ROSBRIDGE_NOT_CONNECTED);
		}

	}

	private static WebSocketMessage<String> createRobotNavigationMessage(Location location) {
		JSONObject position = new JSONObject();
		position.put("x", location.getPositionX());
		position.put("y", location.getPositionY());
		position.put("z", location.getPositionZ());

		JSONObject orientation = new JSONObject();
		orientation.put("x", location.getOrientationX());
		orientation.put("y", location.getOrientationY());
		orientation.put("z", location.getOrientationZ());
		orientation.put("w", location.getOrientationW());

		JSONObject pose = new JSONObject();
		pose.put("position", position);
		pose.put("orientation", orientation);

		JSONObject header = new JSONObject();
		header.put("frame_id", "map");

		JSONObject msg = new JSONObject();
		msg.put("op", "publish");
		msg.put("topic", "/move_base_simple/goal");
		msg.put("msg", new JSONObject().put("header", header).put("pose", pose));
		return new TextMessage(msg.toString());
	}

	private static WebSocketMessage<String> subscribeMoveBaseStatusTopic() {
		JSONObject message = new JSONObject();
		message.put("op", "subscribe");
		message.put("topic", "/move_base/status");
		return new TextMessage(message.toString());
	}

	private static WebSocketMessage<String> subscribeAmclTopic() {
		JSONObject message = new JSONObject();
		message.put("op", "subscribe");
		message.put("topic", "/amcl_pose");
		return new TextMessage(message.toString());
	}

	private static WebSocketMessage<String> subscribeMoveBaseGoalTopic() {
		JSONObject message = new JSONObject();
		message.put("op", "subscribe");
		message.put("topic", "/move_base/goal");
		return new TextMessage(message.toString());
	}

	private static WebSocketMessage<String> advertiseMapTopic() {
		JSONObject message = new JSONObject();
		message.put("op", "advertise");
		message.put("topic", "/send/map_data");
		message.put("type", "std_msgs/String");
		return new TextMessage(message.toString());
	}

	private static WebSocketMessage<String> subscribeMapTopic() {
		JSONObject message = new JSONObject();
		message.put("op", "subscribe");
		message.put("topic", "/send/map_data");
		return new TextMessage(message.toString());
	}
}
