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

        Message msg = new Gson().fromJson(message.getPayload(), Message.class);
        try {
            processMessage(session.getId(), msg);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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

    public void processMessage(String sessionId, Message message) throws IOException {
        if (!clientRecords.containsKey(sessionId)) {
            log.error("Could not find client record with session id: " + sessionId);
            return;
        }

        if (message.getType() == 0) { // broadcast message
            broadcastMessage(message, sessionId);
            return;
        }

        ClientRecord record = clientRecords.get(sessionId);
        record.setName(message.getSender());

        if (message.getType() == 1) { // set initial name
            record.setName(message.getSender());
        } else if (message.getType() == 2) { // set online
            record.setIsOnline(true);
        } else if (message.getType() == 3) { // set dnd
            record.setIsOnline(false);
        }

        clientRecords.put(sessionId, record);
    }

    private void broadcastMessage(Message message, String senderSessionId) throws IOException {
        TextMessage textMessage = new TextMessage(new Gson().toJson(message, Message.class));
        log.info("Sending message to all ws clients: " + textMessage.getPayload());

        for (WebSocketSession webSocketSession : sessions) {
            String sessionId = webSocketSession.getId();
            if (!clientRecords.containsKey(sessionId)) {
                log.error("Could not find client record with session id: " + sessionId);
                continue;
            }
            ClientRecord record = clientRecords.get(sessionId);

            if (sessionId.equals(senderSessionId) || record.getIsOnline()) {
                webSocketSession.sendMessage(textMessage);
            }
        }
    }
}
