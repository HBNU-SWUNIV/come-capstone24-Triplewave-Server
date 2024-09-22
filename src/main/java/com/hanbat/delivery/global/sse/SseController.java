package com.hanbat.delivery.global.sse;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SseController {
	private static final Long EMITTER_EXPIRATION_TIME = 60 * 1000L;
	private final SseEmitters sseEmitters;

	@GetMapping(value = "/robot/position", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> connect() throws IOException {
		SseEmitter emitter = new SseEmitter(EMITTER_EXPIRATION_TIME);
		sseEmitters.addEmitter(emitter);
		try {
			// 처음 SSE 연결 시 더미 데이터를 전달
			emitter.send(SseEmitter.event()
				.name("connect")
				.data("connected!"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return ResponseEntity.ok(emitter);
	}

}
