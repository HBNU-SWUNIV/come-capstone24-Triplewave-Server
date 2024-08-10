package com.hanbat.delivery.global.websocket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RosWebSocketHandler implements WebSocketHandler {
	@Override
	public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
		log.info("Connected to ROSBridge server");
	}

	@Override
	public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws
		Exception {
		log.info("Received message from ROSBridge server: " + webSocketMessage.getPayload());

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
