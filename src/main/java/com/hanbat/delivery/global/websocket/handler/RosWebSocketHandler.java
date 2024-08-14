package com.hanbat.delivery.global.websocket.handler;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RosWebSocketHandler implements WebSocketHandler {
	String currentGoalId = null;

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

		// odom 토픽에서 받은 메세지 파싱
		if (jsonMessage.containsKey("topic") && jsonMessage.get("topic").equals("/odom")) {
			JSONObject odomMsg = (JSONObject)jsonMessage.get("msg");
			JSONObject poseKey = (JSONObject)odomMsg.get("pose");
			JSONObject poseValue = (JSONObject)poseKey.get("pose");
			JSONObject position = (JSONObject)poseValue.get("position");
			log.info("Position X: " + position.get("x"));
			log.info("Position Y: " + position.get("y"));
			log.info("Position Z: " + position.get("z"));

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
					}

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
		log.info("Disconnected from ROSBridge server");
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}


}
