package cn.com.pfu.ws.service;

import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@ServerEndpoint("/webSocket")
@Slf4j
public class Websocket {

    private Session session;

    private static CopyOnWriteArraySet<Websocket> websocket = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        websocket.add(this);
        log.info("【websocket消息】有新的连接，总数：{}", websocket.size());
    }

    @OnClose
    public void onClose() {
        websocket.remove(this);
        log.info("【websocket消息】连接断开，总数：{}", websocket.size());
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket消息】，收到客户端发来的消息:{}", message);
    }

    public void sendMessage(String message) {
        for (Websocket websocket : websocket) {
            log.info("【websocket消息】广播消息:{}", message);
            try {
                websocket.session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
}
