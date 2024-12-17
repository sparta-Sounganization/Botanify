package com.sounganization.botanify.domain.chat.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.chat.entity.ChatRoom;
import com.sounganization.botanify.domain.chat.repository.ChatRoomRepository;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoom createChatRoom(Long senderUserId, Long receiverUserId) {

        userRepository.findById(senderUserId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.USER_NOT_FOUND));
        userRepository.findById(receiverUserId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.USER_NOT_FOUND));

        return chatRoomRepository.findRoomByUsers(senderUserId, receiverUserId)
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.builder()
                        .senderUserId(senderUserId)
                        .receiverUserId(receiverUserId)
                        .build()));
    }

    @Transactional(readOnly = true)
    public Page<ChatRoom> getUserChatRooms(Long userId, Pageable pageable) {

        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.USER_NOT_FOUND));

        return chatRoomRepository.findRoomsByUserIdWithPagination(userId, pageable);
    }

    @Transactional(readOnly = true)
    public ChatRoom getChatRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.getSenderUserId().equals(userId) && !chatRoom.getReceiverUserId().equals(userId)) {
            throw new CustomException(ExceptionStatus.UNAUTHORIZED_CHAT_ACCESS);
        }

        return chatRoom;
    }
}
