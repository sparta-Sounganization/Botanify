package com.sounganization.botanify.common.config.websocket.handler;

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
    private final ConnectionFailureHandler connectionFailureHandler;

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("WebSocket 메시지를 수신했습니다: {}", payload);

        ChatMessageReqDto chatMessage = objectMapper.readValue(payload, ChatMessageReqDto.class);

        try {
            switch (chatMessage.type()) {
                case ENTER:
                    log.info("사용자 {}이(가) 방 {}에 입장합니다.", chatMessage.senderId(), chatMessage.roomId());
                    webSocketChatService.handleEnterRoom(session, chatMessage.roomId(), chatMessage.senderId());
                    break;
                case TALK:
                    if (!session.isOpen()) {
                        log.warn("메시지를 보내는 중 세션이 종료되었습니다.");
                        connectionFailureHandler.handleConnectionFailure(chatMessage);
                        return;
                    }
                    log.info("사용자 {}이(가) 방 {}에서 TALK 메시지를 처리 중입니다.",
                            chatMessage.senderId(), chatMessage.roomId());
                    webSocketChatService.handleChatMessage(chatMessage);
                    break;
                case LEAVE:
                    log.info("사용자 {}이(가) 방 {}에서 나갑니다.", chatMessage.senderId(), chatMessage.roomId());
                    webSocketChatService.handleLeaveRoom(chatMessage.roomId(), chatMessage.senderId());
                    break;
            }
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: ", e);
            handleError(session, e);
            if (chatMessage.type() == ChatMessageReqDto.MessageType.TALK) {
                connectionFailureHandler.handleConnectionFailure(chatMessage);
            }
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
