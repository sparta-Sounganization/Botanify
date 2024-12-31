package com.sounganization.botanify.domain.community.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.entity.QPost;
import com.sounganization.botanify.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> findAllByDetailedQuery(
            Pageable pageable,
            String sortBy,
            String order,
            String city,
            String town,
            String search,
            LocalDate dateBefore
    ) {
        QPost post = QPost.post;
        QUser user = QUser.user;

        // 조건 객체 생성
        BooleanBuilder whereClause = new BooleanBuilder();

        // 컨텐트 정렬 객체 생성
        OrderSpecifier<?> orderSpec = null;

        // 컨텐트 쿼리 양식 생성
        JPAQuery<Post> contentQuery = queryFactory
                .selectFrom(post)
                .orderBy()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 토탈 쿼리 양식 생성
        JPAQuery<Long> totalQuery = queryFactory
                .select(post.count()).from(post);

        // ------------------- v 설정 v ----------------------

        // SoftDelete 기본 조건
        whereClause.and(post.deletedYn.isFalse());

        // 검색어 조건
        if (Objects.nonNull(search) && !search.isEmpty()) {
            whereClause.and(post.title.containsIgnoreCase(search)
                    .or(post.content.containsIgnoreCase(search)));
        }

        // 날짜 지정 조건
        if (Objects.nonNull(dateBefore)) {
            whereClause.and(post.createdAt.loe(dateBefore.atStartOfDay()));
        }

        // 지역 게시판 조건
        if (city != null && !city.isEmpty() && town != null && !town.isEmpty()) {
            contentQuery.leftJoin(user).on(post.userId.eq(user.id));
            totalQuery.leftJoin(user).on(post.userId.eq(user.id));
            whereClause.and(user.city.eq(city).and(user.town.eq(town)));
        }

        if (Objects.nonNull(sortBy)) {
            Order o = "asc".equalsIgnoreCase(order) ? Order.ASC : Order.DESC;
            orderSpec = switch(sortBy) {
                case "createdAt" -> new OrderSpecifier<>(o, post.createdAt);
                case "viewCounts" -> new OrderSpecifier<>(o, post.viewCounts);
                default -> throw new CustomException(ExceptionStatus.INVALID_PARAMETER);
            };
        }

        // ------------------- v 실행 v ----------------------

        // 메인 쿼리 & 총 페이지 수 쿼리 실행 및 반환
        List<Post> content = contentQuery.where(whereClause).orderBy(orderSpec).fetch();
        Long total = totalQuery.where(whereClause).fetchOne();
        return new PageImpl<>(content, pageable, Objects.nonNull(total) ? total : 0);
    }
}
