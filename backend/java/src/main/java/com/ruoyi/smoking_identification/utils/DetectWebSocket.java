package com.ruoyi.smoking_identification.utils; // 包名与 VedoController 一致，方便引用

import com.ruoyi.smoking_identification.handler.WebSocketCorsConfigurator;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

// WebSocket 端点：前端通过 ws://ip:port/video/ws/detect 连接
@ServerEndpoint(value = "/video/ws/detect", configurator = WebSocketCorsConfigurator.class)
@Component
public class DetectWebSocket {
    // 存储所有在线客户端连接（线程安全）
    private static final CopyOnWriteArraySet<DetectWebSocket> WEB_SOCKET_SET = new CopyOnWriteArraySet<>();
    private Session session;

    // 连接建立时触发
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        WEB_SOCKET_SET.add(this);
        System.out.println("WebSocket新连接：" + session.getId() + "，当前在线数：" + WEB_SOCKET_SET.size());
    }

    // 连接关闭时触发
    @OnClose
    public void onClose() {
        WEB_SOCKET_SET.remove(this);
        System.out.println("WebSocket连接关闭：" + session.getId() + "，当前在线数：" + WEB_SOCKET_SET.size());
    }

    // 接收客户端消息（可选，此处无需处理）
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("收到客户端[" + session.getId() + "]消息：" + message);
    }

    // 异常处理
    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket错误：" + session.getId());
        error.printStackTrace();
        WEB_SOCKET_SET.remove(this);
    }

    // 群发识别结果（供 VedoController 调用）
    public static void broadcast(String result) {
        for (DetectWebSocket webSocket : WEB_SOCKET_SET) {
            try {
                // 同步推送消息到客户端
                webSocket.session.getBasicRemote().sendText(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}