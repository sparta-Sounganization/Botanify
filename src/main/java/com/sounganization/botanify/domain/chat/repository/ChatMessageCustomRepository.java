package com.sounganization.botanify.domain.chat.repository;

import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageCustomRepository {
    Page<ChatMessage> findMessagesByRoomIdWithPagination(Long roomId, Pageable pageable);
    int softDeleteMessagesOlderThan(LocalDateTime cutoffDate, int batchSize);
    List<ChatMessage> findUndeliveredMessages();
    Optional<ChatMessage> findDuplicateMessage(Long roomId, Long senderId, String content, LocalDateTime since);
}
