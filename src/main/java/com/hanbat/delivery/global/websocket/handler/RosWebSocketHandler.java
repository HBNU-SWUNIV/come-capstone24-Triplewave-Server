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
