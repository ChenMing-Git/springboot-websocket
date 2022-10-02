package cn.com.pfu.ws.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/socket/screen")
@Component
@Slf4j
public class WebSocketHandler implements InitializingBean {

    private Session session;

    private static CopyOnWriteArraySet<WebSocketHandler> websocket = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        websocket.add(this);
        // 如果连接大于多少 则拒绝连接
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
        try {
            this.session.getBasicRemote().sendText("这里什么都没有，来到一片荒原 T.T");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        log.info("【websocket消息】广播消息");
        // 可以在状态变更的时候使用redis的消息订阅，让这里的线程一直等待消费redis的消息，通知这里的sendMessage，
        // 让其主动查询并通知浏览器。这是一个问题，需要研究一下
        if (websocket.size() > 0) {
            // 有连接的情况才发送
            for (WebSocketHandler websocket : websocket) {
                try {
                    websocket.session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Runnable runnable = new Runnable() {
        @SneakyThrows(InterruptedException.class)
        @Override
        public void run() {
            // 线程不中断的情况下
            while (!Thread.currentThread().isInterrupted()) {
                sendMessage("定时发送");
                Thread.sleep(60 * 1000);
            }
        }
    };

    @Override
    public void afterPropertiesSet() {
        final Thread thread = new Thread(runnable);
        thread.start();
    }
}