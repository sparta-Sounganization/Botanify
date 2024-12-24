package com.sounganization.botanify.domain.community.repository;

import com.sounganization.botanify.domain.community.entity.Comment;


import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentCustomRepository {
    List<Comment> findCommentsByPostId(Long postId);
    Map<Long, Long> countCommentsByPostIds(List<Long> postIds);
    Optional<Comment> findCommentsById(Long commentId);
}
