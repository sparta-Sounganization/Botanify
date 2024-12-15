package com.sounganization.botanify.domain.community.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sounganization.botanify.domain.community.entity.QViewHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class ViewHistoryCustomRepositoryImpl implements ViewHistoryCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existViewHistory(Long postId, Long userId, LocalDate viewedAt) {
        QViewHistory viewHistory = QViewHistory.viewHistory;

        long count = jpaQueryFactory.selectFrom(viewHistory)
                .where(viewHistory.postId.eq(postId)
                        .and(viewHistory.userId.eq(userId))
                        .and(viewHistory.viewedAt.eq(viewedAt)))
                .fetchCount();
        return count > 0;
    }
}
