package com.sounganization.botanify.common.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final ChatWebSocketHandler webSocketHandler;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel());
            String messageBody = new String(message.getBody());
            ChatMessageReqDto chatMessage = objectMapper.readValue(messageBody, ChatMessageReqDto.class);

            // WebSocket client로 전달
            webSocketHandler.broadcastMessage(chatMessage.roomId(), chatMessage);
        } catch (Exception e) {
            log.error("채팅 메시지 처리 중 오류 발생: {}", e.getMessage());
        }
    }
}