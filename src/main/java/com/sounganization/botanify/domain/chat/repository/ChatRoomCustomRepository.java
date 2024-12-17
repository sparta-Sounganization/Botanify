package com.sounganization.botanify.domain.chat.repository;

import com.sounganization.botanify.domain.chat.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomCustomRepository {
    List<ChatRoom> findRoomsByUserId(Long userId);
    Optional<ChatRoom> findRoomByUsers(Long senderUserId, Long receiverUserId);
    List<ChatRoom> findRoomsWithMessagesById(Long userId);
}
