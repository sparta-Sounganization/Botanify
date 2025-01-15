package com.sounganization.botanify.domain.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sounganization.botanify.domain.chat.entity.ChatMessage;
import com.sounganization.botanify.domain.chat.entity.QChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatMessageCustomRepositoryImpl implements ChatMessageCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ChatMessage> findMessagesByRoomIdWithPagination(Long roomId, Pageable pageable) {
        QChatMessage message = QChatMessage.chatMessage;

        List<ChatMessage> messages = jpaQueryFactory
                .selectFrom(message)
                .where(message.chatRoom.id.eq(roomId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(message.createdAt.desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(message.count())
                .from(message)
                .where(message.chatRoom.id.eq(roomId))
                .fetchOne();

        return new PageImpl<>(messages, pageable, total != null ? total : 0L);
    }

    @Override
    public int softDeleteMessagesOlderThan(LocalDateTime cutoffDate, int batchSize) {
        QChatMessage message = QChatMessage.chatMessage;

        List<Long> messageIds = jpaQueryFactory
                .select(message.id)
                .from(message)
                .where(message.createdAt.before(cutoffDate)
                        .and(message.deletedYn.isFalse()))
                .limit(batchSize)
                .fetch();

        if (messageIds.isEmpty()) {
            return 0;
        }

        return (int) jpaQueryFactory
                .update(message)
                .set(message.deletedYn, true)
                .set(message.deletedAt, LocalDateTime.now())
                .where(message.id.in(messageIds))
                .execute();
    }

    @Override
    public List<ChatMessage> findUndeliveredMessages() {
        QChatMessage message = QChatMessage.chatMessage;

        return jpaQueryFactory
                .selectFrom(message)
                .where(message.delivered.isFalse()
                        .and(message.deletedYn.isFalse())
                        .and(message.createdAt.after(LocalDateTime.now().minusHours(24))))
                .orderBy(message.createdAt.asc())
                .fetch();
    }

    @Override
    public Optional<ChatMessage> findDuplicateMessage(Long roomId, Long senderId, String content, LocalDateTime since) {
        QChatMessage message = QChatMessage.chatMessage;

        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(message)
                .where(message.chatRoom.id.eq(roomId)
                        .and(message.senderId.eq(senderId))
                        .and(message.content.eq(content))
                        .and(message.createdAt.goe(since))
                        .and(message.deletedYn.isFalse()))
                .orderBy(message.createdAt.desc())
                .fetchFirst());
    }
}
