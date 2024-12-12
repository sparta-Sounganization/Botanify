package com.sounganization.botanify.domain.community.repository;

import com.sounganization.botanify.domain.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByDeletedYnFalse(Pageable pageable);
}
