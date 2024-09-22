package com.hanbat.delivery.global.sse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanbat.delivery.global.websocket.dto.MapResponse;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Getter
public class SseEmitters {

	// thread-safe한 자료구조(CopyOnWriteArrayList)를 사용하지 않으면 ConcurrnetModificationException 발생 가능성 존재
	private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
	private JSONObject currentPosition;

	public SseEmitter addEmitter(SseEmitter emitter) {
		this.emitters.add(emitter);
		log.info("new emitter added: {}", emitter);
		log.info("emitter list size: {}", emitters.size());

		// 타임아웃이 발생하면 브라우저에서 재연결 요청 -> 새로운 Emitter 객체를 다시 생성하므로 기존 emitter는 리스트에서 삭제
		emitter.onCompletion(() -> {
			log.info("onCompletion callback");
			this.emitters.remove(emitter);
		});

		emitter.onTimeout(() -> {
			log.info("onTimeout callback");
			emitter.complete();
		});

		return emitter;
	}

	public void updatePosition(JSONObject position) {
		this.currentPosition = position;
		sendPosition(position);
	}


	public void sendPosition(JSONObject position) {
		for (SseEmitter emitter : emitters) {
			CompletableFuture.runAsync(() -> {
				try {
					emitter.send(SseEmitter.event()
						.name("positionUpdate")
						.data(position.toString()));
				} catch (IOException e) {
					handleEmitterError(emitter);
				}
			});
		}
	}

	public void sendDeliveryCompleted() {
		for (SseEmitter emitter : emitters) {
			CompletableFuture.runAsync(() -> {
				try {
					emitter.send(SseEmitter.event()
						.name("deliveryCompleted")
						.data("배달이 완료되었습니다!"));
				} catch (IOException e) {
					handleEmitterError(emitter);
				}
			});
		}
	}

	public void sendMapData(MapResponse mapResponse) {
		String jsonStr = convertToJson(mapResponse);
		for (SseEmitter emitter : emitters) {
			CompletableFuture.runAsync(() -> {
				try {
					emitter.send(SseEmitter.event()
						.name("sendMapData")
						.data(jsonStr, MediaType.APPLICATION_JSON));
				} catch (IOException e) {
					handleEmitterError(emitter);
				}
			});
		}
	}

	private String convertToJson(MapResponse mapResponse) {
		// Use a JSON library like Jackson to convert MapResponse to JSON string
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(mapResponse);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			return "{}";
		}
	}

	private void handleEmitterError(SseEmitter emitter) {
		try {
			emitter.completeWithError(new Exception("Error sending SSE event"));
		} finally {
			emitters.remove(emitter);
		}
	}

}
