package com.sounganization.botanify.domain.chat.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.entity.ChatRoom;
import com.sounganization.botanify.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final TransactionTemplate transactionTemplate;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public ChatMessage createMessage(Long roomId, Long senderId, String content) {
        log.debug("메시지 생성 중 - roomId: {}, senderId: {}", roomId, senderId);

        Optional<ChatMessage> existingMessage = chatMessageRepository
                .findDuplicateMessage(roomId, senderId, content, LocalDateTime.now().minusSeconds(10));

        if (existingMessage.isPresent()) {
            log.debug("중복 메시지를 발견했습니다. 기존 메시지를 반환합니다.");
            return existingMessage.get();
        }

        String dedupeKey = String.format("msg:dedupe:%d:%d:%d",
                roomId, senderId, content.hashCode());

        Boolean isNew = redisTemplate.opsForValue()
                .setIfAbsent(dedupeKey, "1", Duration.ofSeconds(10));

        if (Boolean.FALSE.equals(isNew)) {
            log.debug("Redis에서 중복 메시지가 감지되었습니다.");
            return null;
        }

        ChatRoom chatRoom = chatRoomService.getChatRoom(roomId, senderId);

        if (!chatRoom.getSenderUserId().equals(senderId) &&
                !chatRoom.getReceiverUserId().equals(senderId)) {
            throw new CustomException(ExceptionStatus.UNAUTHORIZED_CHAT_ACCESS);
        }

        return ChatMessage.builder()
                .type(ChatMessage.MessageType.TALK)
                .senderId(senderId)
                .content(content)
                .chatRoom(chatRoom)
                .delivered(false)
                .build();
    }

    @Transactional
    public void markMessageAsDelivered(Long messageId) {
        try {
            transactionTemplate.execute(status -> {
                chatMessageRepository.findById(messageId).ifPresent(message -> {
                    message.markAsDelivered();
                    chatMessageRepository.save(message);
                    log.debug("메시지 {}를 전달됨으로 표시했습니다.", messageId);
                });
                return null;
            });
        } catch (Exception e) {
            log.error("메시지를 전달됨으로 표시하는 데 실패했습니다: {}", e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public ChatMessage findExistingMessage(Long roomId, Long senderId, String content) {
        return chatMessageRepository.findDuplicateMessage(
                roomId,
                senderId,
                content,
                LocalDateTime.now().minusSeconds(30)
        ).orElse(null);
    }

    @Async("chatThreadPoolTaskExecutor")
    public Future<ChatMessage> saveMessageAsync(ChatMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return transactionTemplate.execute(status -> {
                    try {
                        ChatMessage saved = chatMessageRepository.save(message);
                        log.info("메시지가 비동기적으로 저장되었습니다. roomId: {}",
                                message.getChatRoom().getId());
                        return saved;
                    } catch (Exception e) {
                        log.error("메시지 저장 중 오류 발생: {}", e.getMessage(), e);
                        status.setRollbackOnly();
                        throw e;
                    }
                });
            } catch (Exception e) {
                log.error("메시지 저장 실패", e);
                throw new CompletionException(e);
            }
        });
    }

    @Transactional(readOnly = true)
    public Page<ChatMessage> getRoomMessages(Long roomId, Long userId, Pageable pageable) {

        chatRoomService.getChatRoom(roomId, userId);

        return chatMessageRepository.findMessagesByRoomIdWithPagination(roomId, pageable);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long userId) {

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.CHAT_MESSAGE_NOT_FOUND));

        if (!message.getSenderId().equals(userId)) {
            throw new CustomException(ExceptionStatus.MESSAGE_NOT_OWNED);
        }

        message.softDelete();
    }
}
