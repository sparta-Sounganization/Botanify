package com.sounganization.botanify.domain.chat.repository;

import com.sounganization.botanify.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChatRoomCustomRepository {
    Optional<ChatRoom> findRoomByUsers(Long senderUserId, Long receiverUserId);
    Page<ChatRoom> findRoomsByUserIdWithPagination(Long userId, Pageable pageable);
    int softDeleteEmptyRoomsOlderThan(LocalDateTime cutoffDate);
}
