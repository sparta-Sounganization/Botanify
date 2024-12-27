package com.sounganization.botanify.common.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.common.config.websocket.service.WebSocketChatService;
import com.sounganization.botanify.common.config.websocket.util.WebSocketUtils;
import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final WebSocketChatService webSocketChatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("새로운 WebSocket 연결이 열렸습니다. 세션 ID: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatMessageReqDto chatMessage = objectMapper.readValue(payload, ChatMessageReqDto.class);

        try {
            switch (chatMessage.type()) {
                case ENTER:
                    webSocketChatService.handleEnterRoom(session, chatMessage.roomId(), chatMessage.senderId());
                    break;
                case TALK:
                    webSocketChatService.handleChatMessage(chatMessage);
                    break;
                case LEAVE:
                    webSocketChatService.handleLeaveRoom(chatMessage.roomId(), chatMessage.senderId());
                    break;
            }
        } catch (Exception e) {
            handleError(session, e);
        }
    }

    private void handleError(WebSocketSession session, Exception e) {
        log.error("WebSocket 에러 발생: {}", e.getMessage());
        WebSocketUtils.closeSession(session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        log.info("WebSocket 연결이 닫혔습니다. 세션 ID: {}", session.getId());
    }
}
