package com.wolf.ai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	/**
	 * 注册stomp的端点
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 允许使用socketJs方式访问，访问点为ws，允许跨域
		// 在网页上我们就可以通过这个链接
		// http://localhost:8080/ws
		// 来和服务器的WebSocket连接
		registry.addEndpoint("/stomp").setAllowedOrigins("*")//.addInterceptors(new SessionAuthHandshakeInterceptor())
				.withSockJS();
	}

	/**
	 * 配置信息代理
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 订阅Broker名称
		registry.enableSimpleBroker("/queue", "/topic");
		// 全局使用的消息前缀（客户端订阅路径上会体现出来）
		registry.setApplicationDestinationPrefixes("/app");
		// 点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
		registry.setUserDestinationPrefix("/user");
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		registry.setMessageSizeLimit(500 * 1024 * 1024);
		registry.setSendBufferSizeLimit(1024 * 1024 * 1024);
		registry.setSendTimeLimit(200000);
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		//registration.interceptors(createUserInterceptor());
	}

}
