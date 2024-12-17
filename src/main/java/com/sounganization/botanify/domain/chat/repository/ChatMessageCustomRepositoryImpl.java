package com.sounganization.botanify.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.entity.ChatRoom;
import com.sounganization.botanify.domain.chat.entity.QChatMessage;
import com.sounganization.botanify.domain.chat.entity.QChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageCustomRepositoryImpl implements ChatMessageCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ChatMessage> findMessagesByRoomId(Long roomId) {
        QChatMessage message = QChatMessage.chatMessage;

        return jpaQueryFactory
                .selectFrom(message)
                .where(message.chatRoom.id.eq(roomId))
                .orderBy(message.createdAt.asc())
                .fetch();
    }

    @Override
    public List<ChatMessage> findRecentMessages(ChatRoom room, LocalDateTime after) {
        QChatMessage message = QChatMessage.chatMessage;

        return jpaQueryFactory
                .selectFrom(message)
                .where(message.chatRoom.eq(room)
                        .and(message.createdAt.after(after)))
                .orderBy(message.createdAt.asc())
                .fetch();
    }

    @Override
    public List<ChatMessage> findMessagesWithRoomByRoomId(Long roomId) {
        QChatMessage message = QChatMessage.chatMessage;
        QChatRoom room = QChatRoom.chatRoom;

        return jpaQueryFactory
                .selectFrom(message)
                .join(message.chatRoom, room)
                .fetchJoin()
                .where(message.chatRoom.id.eq(roomId))
                .orderBy(message.createdAt.asc())
                .fetch();
    }
}