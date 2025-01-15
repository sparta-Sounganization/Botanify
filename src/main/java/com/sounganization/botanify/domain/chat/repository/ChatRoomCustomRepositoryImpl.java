package com.sounganization.botanify.domain.chat.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sounganization.botanify.domain.chat.entity.ChatRoom;
import com.sounganization.botanify.domain.chat.entity.QChatMessage;
import com.sounganization.botanify.domain.chat.entity.QChatRoom;
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
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<ChatRoom> findRoomByUsers(Long senderUserId, Long receiverUserId) {
        QChatRoom chatRoom = QChatRoom.chatRoom;

        ChatRoom result = jpaQueryFactory
                .selectFrom(chatRoom)
                .where(chatRoom.senderUserId.eq(senderUserId)
                        .and(chatRoom.receiverUserId.eq(receiverUserId)))
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<ChatRoom> findRoomsByUserIdWithPagination(Long userId, Pageable pageable) {
        QChatRoom chatRoom = QChatRoom.chatRoom;

        List<ChatRoom> rooms = jpaQueryFactory
                .selectFrom(chatRoom)
                .where(chatRoom.senderUserId.eq(userId)
                        .or(chatRoom.receiverUserId.eq(userId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(chatRoom.updatedAt.desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(chatRoom.count())
                .from(chatRoom)
                .where(chatRoom.senderUserId.eq(userId)
                        .or(chatRoom.receiverUserId.eq(userId)))
                .fetchOne();

        return new PageImpl<>(rooms, pageable, total != null ? total : 0L);
    }

    @Override
    public int softDeleteEmptyRoomsOlderThan(LocalDateTime cutoffDate) {
        QChatRoom room = QChatRoom.chatRoom;
        QChatMessage message = QChatMessage.chatMessage;

        return (int) jpaQueryFactory
                .update(room)
                .set(room.deletedYn, true)
                .set(room.deletedAt, LocalDateTime.now())
                .where(room.updatedAt.before(cutoffDate)
                        .and(room.deletedYn.isFalse())
                        .and(JPAExpressions
                                .select(message.count())
                                .from(message)
                                .where(message.chatRoom.eq(room)
                                        .and(message.deletedYn.isFalse()))
                                .eq(0L)))
                .execute();
    }
}
