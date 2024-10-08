package com.hanbat.delivery.global.websocket.handler;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.hanbat.delivery.domain.request.application.RequestService;
import com.hanbat.delivery.global.sse.SseEmitters;
import com.hanbat.delivery.global.websocket.dto.MapResponse;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RosWebSocketHandler implements WebSocketHandler {

	private final SseEmitters sseEmitters;

	private final RequestService requestService;
	String currentGoalId = null;

	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	Boolean canSendPosition = true;
	Boolean navigationCompleted = false;

	public RosWebSocketHandler(SseEmitters sseEmitters, RequestService requestService) {
		this.sseEmitters = sseEmitters;
		this.requestService = requestService;
		// 3초마다 위치 전송
		scheduler.scheduleAtFixedRate(() -> canSendPosition = true, 0, 3, TimeUnit.SECONDS);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
		log.info("Connected to ROSBridge server");
	}

	@Override
	public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws
		Exception {
		log.info("Received message from ROSBridge server: " + webSocketMessage.getPayload());
		JSONParser parser = new JSONParser();
		JSONObject jsonMessage = (JSONObject)parser.parse(webSocketMessage.getPayload().toString());

		// log.info(jsonMessage.toString());

		if (jsonMessage.containsKey("topic") && jsonMessage.get("topic").equals("/send/map_data")) {
			JSONObject msg = (JSONObject)jsonMessage.get("msg");
			String data = msg.get("data").toString();

			// Base64 디코딩
			byte[] compressedData = Base64.getDecoder().decode(data);

			// 압축 해제
			String jsonString = decompress(compressedData);
			// log.info(jsonString);

			// JSON 문자열을 JSON 객체로 변환
			JSONObject jsonMapData = (JSONObject)parser.parse(jsonString);

			JSONArray mapDataArray = (JSONArray)jsonMapData.get("data");

			// JSONArray를 List<Integer>로 변환
			List<Integer> integerList = new ArrayList<>();
			for (Object o : mapDataArray) {
				integerList.add(Integer.parseInt(o.toString()));
			}

			// orgin.x, origin.y 받기
			JSONObject originMapData = (JSONObject)jsonMapData.get("origin");

			MapResponse mapResponse = MapResponse.builder()
				.width(Long.parseLong(jsonMapData.get("width").toString()))
				.height(Long.parseLong(jsonMapData.get("height").toString()))
				.resolution(Double.parseDouble(jsonMapData.get("resolution").toString()))
				.origin(originMapData)
				.data(integerList)
				.build();

			log.info("Decoded and decompressed data: " + mapResponse.getResolution().toString());
			sseEmitters.sendMapData(mapResponse);

		}
		// odom 토픽에서 받은 메세지 파싱
		if (jsonMessage.containsKey("topic") && jsonMessage.get("topic").equals("/amcl_pose")) {
			JSONObject odomMsg = (JSONObject)jsonMessage.get("msg");
			JSONObject poseKey = (JSONObject)odomMsg.get("pose");
			JSONObject poseValue = (JSONObject)poseKey.get("pose");
			JSONObject position = (JSONObject)poseValue.get("position");
			log.info("Position X: " + position.get("x"));
			// log.info("Position Y: " + position.get("y"));
			// log.info("Position Z: " + position.get("z"));

			// 위치 데이터를 SseEmitters에 전달
			if (canSendPosition) {
				canSendPosition = false;
				sseEmitters.updatePosition(position);
			}
		}

		// /move_base/goal 토픽에서 받은 메세지 파싱
		if (jsonMessage.containsKey("topic") && jsonMessage.get("topic").equals("/move_base/goal")) {
			JSONObject goalMsg = (JSONObject)jsonMessage.get("msg");
			log.info(goalMsg.toString());
		}
		// /move_base/status 토픽에서 받은 메세지 파싱
		if (jsonMessage.containsKey("topic") && jsonMessage.get("topic").equals("/move_base/status")) {
			JSONObject statusMsg = (JSONObject)jsonMessage.get("msg");
			JSONArray statusList = (JSONArray)statusMsg.get("status_list");

			if (!statusList.isEmpty()) {
				for (int i = 0; i < statusList.size(); i++) {
					JSONObject statusObject = (JSONObject)statusList.get(i);
					Long status = (Long)statusObject.get("status");
					log.info(status.toString());

					// status가 1이면, 새로 생긴 네비게이션이므로, 해당 goalId를 파싱
					if (status == 1 && currentGoalId == null) {
						JSONObject goalIdObject = (JSONObject)statusObject.get("goal_id");
						currentGoalId = goalIdObject.get("id").toString();
						log.info("Goal ID with status 1 : " + currentGoalId);
					}

					JSONObject goalIdObject = (JSONObject)statusObject.get("goal_id");
					String goalId = goalIdObject.get("id").toString();
					log.info("Parsing Goal Id : " + goalId);
					log.info("Current Goal Id : " + currentGoalId);

					// 해당 goalId인 네비게이션이고, 도착했다면,
					if (status == 3 && goalId.equals(currentGoalId)) {
						// odom 토픽 구독 해제
						JSONObject unsubscribeMessageOdom = new JSONObject();
						unsubscribeMessageOdom.put("op", "unsubscribe");
						unsubscribeMessageOdom.put("topic", "/odom");
						webSocketSession.sendMessage(new TextMessage(unsubscribeMessageOdom.toString()));
						log.info("Unsubscribe Odom Topic");

						// status 토픽 구독 해제
						JSONObject unsubscribeMessageStatus = new JSONObject();
						unsubscribeMessageStatus.put("op", "unsubscribe");
						unsubscribeMessageStatus.put("topic", "/move_base/status");
						webSocketSession.sendMessage(new TextMessage(unsubscribeMessageStatus.toString()));
						log.info("Unsubscribe move_base/status Topic");
						log.info("Navigation Completed");
						navigationCompleted = true;
						break;
					}

				}
				if (navigationCompleted) {
					sseEmitters.sendDeliveryCompleted();
					requestService.updateRequestStatus();
				}
			}
		}
	}


	@Override
	public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
		log.error("WebSocket error: " + throwable.getMessage());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
		log.info(closeStatus.getReason());
		log.info("Disconnected from ROSBridge server");
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	private static String decompress(byte[] compressedData) throws IOException, DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(compressedData);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compressedData.length);
		byte[] buffer = new byte[1024];
		while (!inflater.finished()) {
			int count = inflater.inflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		inflater.end();

		return new String(outputStream.toByteArray(), "UTF-8");
	}

}
