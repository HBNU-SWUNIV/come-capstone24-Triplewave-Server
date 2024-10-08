package com.hanbat.delivery.global.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hanbat.delivery.domain.request.application.RequestService;
import com.hanbat.delivery.global.sse.SseEmitters;
import com.hanbat.delivery.global.websocket.handler.RosWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

	private final SseEmitters sseEmitters;
	private final RequestService requestService;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
		webSocketHandlerRegistry.addHandler(rosWebSocketHandler());
	}
	
	public WebSocketHandler rosWebSocketHandler() {
		return new RosWebSocketHandler(sseEmitters, requestService);
	}
	
	@Bean
	WebMvcConfigurer corsConfig() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/ws/**")
					.allowedMethods("GET", "POST", "PUT", "DELETE")
					.allowedHeaders("*")
					.allowedOrigins("*");
			}
		};
	}
}
