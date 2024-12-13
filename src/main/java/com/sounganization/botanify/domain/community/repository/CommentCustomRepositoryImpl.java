package com.sounganization.botanify.domain.community.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.QComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
