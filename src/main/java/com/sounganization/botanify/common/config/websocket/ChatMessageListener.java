package com.sounganization.botanify.common.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.service.ChatMessageService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final ChatMessageService chatMessageService;
    private final Set<String> processedMessages = ConcurrentHashMap.newKeySet();

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        try {
            String messageContent = new String(message.getBody(), StandardCharsets.UTF_8);
            if (!processedMessages.add(messageContent)) {
                log.debug("Listener를 통해 이미 처리된 메시지입니다. 건너뜁니다.");
                return;
            }

            ChatMessageReqDto chatMessage = objectMapper.readValue(messageContent, ChatMessageReqDto.class);

            if (chatMessage.source() != ChatMessageReqDto.MessageSource.WEBSOCKET) {
                log.debug("WebSocket 메시지가 아니므로 건너뜁니다.");
                return;
            }

            ChatMessage savedMessage = chatMessageService.findExistingMessage(
                    chatMessage.roomId(),
                    chatMessage.senderId(),
                    chatMessage.content()
            );

            if (savedMessage != null) {
                chatMessageService.markMessageAsDelivered(savedMessage.getId());
                log.debug("브로드캐스트 후 메시지가 전달됨으로 표시되었습니다 - messageId: {}", savedMessage.getId());
            }

            removeProcessedMessageAfterDelay(messageContent);

        } catch (Exception e) {
            log.error("Redis Pub/Sub에서 메시지 처리 실패", e);
        }
    }

    private void removeProcessedMessageAfterDelay(String messageContent) {
        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(() -> {
            processedMessages.remove(messageContent);
        });
    }
}
