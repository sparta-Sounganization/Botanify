package com.sounganization.botanify.common.config.websocket.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.common.config.websocket.util.WebSocketUtils;
import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageBroadcastService {

    private final ObjectMapper objectMapper;
    private final Map<Long, Map<Long, WebSocketSession>> chatRoomSessions = new ConcurrentHashMap<>();

    public void broadcastMessage(Long roomId, ChatMessageReqDto message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            WebSocketUtils.broadcastMessageToRoom(roomId, messageJson, chatRoomSessions);
        } catch (JsonProcessingException e) {
            log.error("메시지 변환 중 오류 발생: {}", e.getMessage());
        }
    }

    public void addSession(Long roomId, Long userId, WebSocketSession session) {
        chatRoomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                .put(userId, session);
    }

    public void removeSession(Long roomId, Long userId) {
        Map<Long, WebSocketSession> roomSessions = chatRoomSessions.get(roomId);
        if (roomSessions != null) {
            WebSocketSession session = roomSessions.remove(userId);
            if (session != null) {
                WebSocketUtils.closeSession(session);
            }
            if (roomSessions.isEmpty()) {
                chatRoomSessions.remove(roomId);
            }
        }
    }
}
