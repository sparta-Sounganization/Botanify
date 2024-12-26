package com.sounganization.botanify.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.chat.dto.req.ChatMessageReqDto;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.entity.ChatRoom;
import com.sounganization.botanify.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public ChatMessage saveMessage(Long roomId, Long senderId, String content) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(roomId, senderId);

        if (!chatRoom.getSenderUserId().equals(senderId) && !chatRoom.getReceiverUserId().equals(senderId)) {
            throw new CustomException(ExceptionStatus.UNAUTHORIZED_CHAT_ACCESS);
        }

        ChatMessage message = ChatMessage.builder()
                .type(ChatMessage.MessageType.TALK)
                .senderId(senderId)
                .content(content)
                .chatRoom(chatRoom)
                .build();

        //Redis로 메시지 발행
        try {
            ChatMessageReqDto messageDto = new ChatMessageReqDto(
                    ChatMessageReqDto.MessageType.TALK,
                    roomId,
                    senderId,
                    content
            );
            redisTemplate.convertAndSend(
                    "chat_room_" + roomId,
                    objectMapper.writeValueAsString(messageDto)
            );
        } catch (Exception e) {
            log.error("메시지 발행 중 오류 발생: {}", e.getMessage());
        }

        // 비동기적으로 DB에 저장
        return CompletableFuture.supplyAsync(() -> chatMessageRepository.save(message))
                .exceptionally(throwable -> {
                    log.error("메시지 저장 중 오류 발생: {}", throwable.getMessage());
                    return message;
                }).join();
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
