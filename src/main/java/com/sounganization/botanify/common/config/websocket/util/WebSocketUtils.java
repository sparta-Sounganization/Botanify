package com.sounganization.botanify.common.config.websocket.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class WebSocketUtils {

    public static void broadcastMessageToRoom(Long roomId, String messageJson,
                                              Map<Long, Map<Long, WebSocketSession>> sessions) {

        TextMessage textMessage = new TextMessage(messageJson);
        Map<Long, WebSocketSession> roomSessions = sessions.get(roomId);
        if (roomSessions != null) {
            roomSessions.values().forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                } catch (IOException e) {
                    log.error("메시지 전송 중 오류 발생: {}", e.getMessage());
                }
            });
        }
    }

    public static boolean isValidSession(WebSocketSession session) {
        return session != null && session.isOpen();
    }

    public static void closeSession(WebSocketSession session) {
        try {
            if (isValidSession(session)) {
                session.close();
            }
        } catch (IOException e) {
            log.error("세션 종료 중 오류 발생: {}", e.getMessage());
        }
    }
}
