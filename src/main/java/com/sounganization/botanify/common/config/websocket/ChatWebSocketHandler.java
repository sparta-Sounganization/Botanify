package com.sounganization.botanify.common.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import com.sounganization.botanify.domain.chat.dto.res.ErrorMessageDto;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.service.ChatMessageService;
import com.sounganization.botanify.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    private final Map<Long, Map<Long, WebSocketSession>> chatRoomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("새로운 WebSocket 연결이 열렸습니다. 세션 ID: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatMessageReqDto chatMessage = objectMapper.readValue(payload, ChatMessageReqDto.class);

        Long roomId = chatMessage.roomId();
        Long userId = chatMessage.senderId();

        try {
            switch (chatMessage.type()) {
                case ENTER:
                    handleEnterMessage(session, roomId, userId);
                    break;
                case TALK:
                    handleChatMessage(session, chatMessage);
                    break;
                case LEAVE:
                    handleLeaveMessage(session, roomId, userId);
                    break;
            }
        } catch (Exception e) {
            handleError(session, e);
        }
    }

    private void handleEnterMessage(WebSocketSession session, Long roomId, Long userId) {

        chatRoomService.getChatRoom(roomId, userId);

        chatRoomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                .put(userId, session);

        log.info("사용자 {}가 채팅방 {}에 입장했습니다.", userId, roomId);
    }

    private void handleChatMessage(WebSocketSession session, ChatMessageReqDto chatMessage) throws IOException {

        ChatMessage savedMessage = chatMessageService.saveMessage(
                chatMessage.roomId(),
                chatMessage.senderId(),
                chatMessage.content()
        );

        String messageJson = objectMapper.writeValueAsString(chatMessage);
        broadcastMessage(savedMessage.getChatRoom().getId(), new TextMessage(messageJson));
    }

    private void handleLeaveMessage(WebSocketSession session, Long roomId, Long userId) {

        if (chatRoomSessions.containsKey(roomId)) {
            chatRoomSessions.get(roomId).remove(userId);
            if (chatRoomSessions.get(roomId).isEmpty()) {
                chatRoomSessions.remove(roomId);
            }
        }

        log.info("사용자 {}가 채팅방 {}에서 퇴장했습니다.", userId, roomId);
    }

    private void broadcastMessage(Long roomId, TextMessage message) {
        Map<Long, WebSocketSession> roomSessions = chatRoomSessions.get(roomId);
        if (roomSessions != null) {
            roomSessions.values().forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(message);
                    }
                } catch (IOException e) {
                    log.error("메시지 전송 중 오류 발생: {}", e.getMessage());
                }
            });
        }
    }

    private void handleError(WebSocketSession session, Exception e) throws IOException {
        log.error("WebSocket 오류 발생: {}", e.getMessage());
        ErrorMessageDto errorMessage = new ErrorMessageDto("오류가 발생했습니다: " + e.getMessage());
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorMessage)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket 연결이 닫혔습니다. 세션 ID: {}", session.getId());
    }
}
