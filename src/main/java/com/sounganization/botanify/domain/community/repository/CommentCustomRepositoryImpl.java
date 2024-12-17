package com.sounganization.botanify.domain.community.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.QComment;
import com.sounganization.botanify.domain.community.entity.QPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> findCommentsByPostId(Long postId) {
        QComment comment = QComment.comment;

        List<Comment> result = jpaQueryFactory.selectFrom(comment)
                .where(comment.post.id.eq(postId)
                        .and(comment.deletedYn.isFalse()))
                .orderBy(
                        comment.parentComment.id.coalesce(comment.id).asc(),
                        comment.id.asc()
                )
                .fetch();
        return List.copyOf(result);
    }

    @Override
    public Map<Long, Long> countCommentsByPostIds(List<Long> postIds) {
        QComment comment = QComment.comment;
        QPost post = QPost.post;

        List<Tuple> results = jpaQueryFactory
                .select(comment.post.id, comment.count())
                .from(comment)
                .join(comment.post, post)
                .where(comment.post.id.in(postIds)
                        .and(comment.deletedYn.isFalse()))
                .groupBy(comment.post.id)
                .fetch();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Long.class),
                        tuple -> tuple.get(1, Long.class),
                        (a, b) -> b
                ));
    }
}
