package com.ruoyi.smoking_identification.handler;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * WebSocket跨域拦截器（JSR-356注解方式专用）
 */
public class WebSocketCorsConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        // 添加跨域响应头，允许所有前端域名（开发环境用）
        response.getHeaders().put("Access-Control-Allow-Origin", java.util.Collections.singletonList("*"));
        response.getHeaders().put("Access-Control-Allow-Credentials", java.util.Collections.singletonList("true"));
    }
}