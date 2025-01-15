package com.sounganization.botanify.common.config.websocket.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.common.config.websocket.util.WebSocketUtils;
import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageBroadcastService {
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final Map<Long, Map<Long, WebSocketSession>> chatRoomSessions = new ConcurrentHashMap<>();
    private final Set<String> processedMessageIds = ConcurrentHashMap.newKeySet();

    public void broadcastMessage(Long roomId, ChatMessageReqDto message) {
        String messageId = generateMessageId(roomId, message);

        if (!processedMessageIds.add(messageId)) {
            log.debug("이미 브로드캐스트된 메시지입니다. 건너뜁니다: {}", messageId);
            return;
        }

        try {
            log.debug("방 {}에 메시지를 브로드캐스트 중입니다.", roomId);
            redisTemplate.convertAndSend(
                    "chat_room_broadcast_" + roomId,
                    objectMapper.writeValueAsString(message)
            );

            removeProcessedMessageIdAfterDelay(messageId);
        } catch (JsonProcessingException e) {
            log.error("Redis에 메시지 게시 실패", e);
            processedMessageIds.remove(messageId);
        }
    }

    private String generateMessageId(Long roomId, ChatMessageReqDto message) {
        return String.format("%d:%d:%d:%s",
                roomId,
                message.senderId(),
                message.content().hashCode(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
    }

    private void removeProcessedMessageIdAfterDelay(String messageId) {
        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(() -> {
            processedMessageIds.remove(messageId);
        });
    }

    public void addSession(Long roomId, Long userId, WebSocketSession session) {
        chatRoomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                .put(userId, session);
        log.debug("roomId: {}, userId: {}에 대한 세션을 추가했습니다.", roomId, userId);
    }

    public void removeSession(Long roomId, Long userId) {
        Map<Long, WebSocketSession> roomSessions = chatRoomSessions.get(roomId);
        if (roomSessions != null) {
            WebSocketSession session = roomSessions.remove(userId);
            if (session != null) {
                WebSocketUtils.closeSession(session);
                log.debug("roomId: {}, userId: {}에 대한 세션을 제거했습니다.", roomId, userId);
            }
            if (roomSessions.isEmpty()) {
                chatRoomSessions.remove(roomId);
                log.debug("빈 방을 제거했습니다: {}", roomId);
            }
        }
    }
}