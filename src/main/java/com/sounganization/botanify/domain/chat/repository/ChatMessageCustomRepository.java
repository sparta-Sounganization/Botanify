package com.sounganization.botanify.domain.chat.repository;

import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageCustomRepository {
    List<ChatMessage> findMessagesByRoomId(Long roomId);
    List<ChatMessage> findRecentMessages(ChatRoom room, LocalDateTime after);
    List<ChatMessage> findMessagesWithRoomByRoomId(Long roomId);
    Page<ChatMessage> findMessagesByRoomIdWithPagination(Long roomId, Pageable pageable);
    int softDeleteMessagesOlderThan(LocalDateTime cutoffDate, int batchSize);
    long countActiveMessagesByRoomId(Long roomId);
    List<ChatMessage> findUndeliveredMessages();
}
