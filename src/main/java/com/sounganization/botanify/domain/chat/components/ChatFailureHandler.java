package com.sounganization.botanify.domain.chat.components;

import com.sounganization.botanify.common.config.websocket.service.WebSocketChatService;
import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatFailureHandler {
    private final ChatMessageRepository chatMessageRepository;
    private final WebSocketChatService webSocketChatService;

    public void handleRedisFailure(ChatMessageReqDto message) {
        log.error("Redis 연결 실패 - Fallback 모드로 전환");
        webSocketChatService.handleChatMessage(message);
    }

    public void handleMessageRecovery() {
        log.info("미전송 메시지 복구 시작");
        try {
            List<ChatMessage> undeliveredMessages = chatMessageRepository
                    .findUndeliveredMessages();

            for (ChatMessage message : undeliveredMessages) {
                retryMessageDelivery(message);
            }
        } catch (Exception e) {
            log.error("메시지 복구 실패: {}", e.getMessage());
        }
    }

    private void retryMessageDelivery(ChatMessage message) {
        ChatMessageReqDto messageDto = new ChatMessageReqDto(
                ChatMessageReqDto.MessageType.TALK,
                message.getChatRoom().getId(),
                message.getSenderId(),
                message.getContent(),
                ChatMessageReqDto.MessageSource.WEBSOCKET
        );

        try {
            webSocketChatService.handleChatMessage(messageDto);
            message.markAsDelivered();
            chatMessageRepository.save(message);
        } catch (Exception e) {
            log.error("메시지 재전송 실패 - messageId: {}", message.getId());
        }
    }
}
