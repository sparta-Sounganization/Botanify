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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

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

        return chatMessageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public Page<ChatMessage> getRoomMessages(Long roomId, Long userId, Pageable pageable) {

        chatRoomService.getChatRoom(roomId, userId);

        return chatMessageRepository.findMessagesByRoomIdWithPagination(roomId, pageable);
    }
}
