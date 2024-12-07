package com.sounganization.botanify.domain.community.repository;

import com.sounganization.botanify.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndDeletedYnFalse(Long postId);
    Optional<Comment> findByIdAndDeletedYnFalse(Long id);
}
