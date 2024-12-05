package com.sounganization.botanify.domain.community.repository;

import com.sounganization.botanify.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
