package edu.baylor.ecs;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class ChatWsHandler extends TextWebSocketHandler {

    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    Map<String, ClientRecord> clientRecords = new HashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("Received message from ws client: " + message.getPayload());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Ws client connected");
        sessions.add(session);
        clientRecords.put(session.getId(), new ClientRecord("", true));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        log.info("Ws client disconnected");
        sessions.remove(session);
        clientRecords.remove(session.getId());
    }
}
