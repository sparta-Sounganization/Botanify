package com.sounganization.botanify.domain.community.repository;

import com.sounganization.botanify.domain.community.entity.Comment;


import java.util.List;

public interface CommentCustomRepository {
    List<Comment> findCommentsByPostId(Long postId);
}
