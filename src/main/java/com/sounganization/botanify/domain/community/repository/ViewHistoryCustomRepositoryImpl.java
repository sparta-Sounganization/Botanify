package com.sounganization.botanify.domain.community.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sounganization.botanify.domain.community.entity.QViewHistory;
import com.sounganization.botanify.domain.community.entity.ViewHistory;
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

        ViewHistory result = jpaQueryFactory.selectFrom(viewHistory)
                .where(viewHistory.postId.eq(postId)
                        .and(viewHistory.userId.eq(userId))
                        .and(viewHistory.viewedAt.eq(viewedAt)))
                .fetchFirst();
        return  result != null;
    }
}
