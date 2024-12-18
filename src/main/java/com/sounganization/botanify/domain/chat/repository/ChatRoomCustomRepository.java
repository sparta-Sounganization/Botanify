package com.sounganization.botanify.domain.chat.repository;

import com.sounganization.botanify.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ChatRoomCustomRepository {
    List<ChatRoom> findRoomsByUserId(Long userId);
    Optional<ChatRoom> findRoomByUsers(Long senderUserId, Long receiverUserId);
    List<ChatRoom> findRoomsWithMessagesById(Long userId);
    Page<ChatRoom> findRoomsByUserIdWithPagination(Long userId, Pageable pageable);
}
