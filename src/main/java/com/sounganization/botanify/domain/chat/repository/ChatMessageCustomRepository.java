package com.sounganization.botanify.domain.chat.repository;

import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.entity.ChatRoom;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageCustomRepository {
    List<ChatMessage> findMessagesByRoomId(Long roomId);
    List<ChatMessage> findRecentMessages(ChatRoom room, LocalDateTime after);
    List<ChatMessage> findMessagesWithRoomByRoomId(Long roomId); // Add this method
}
