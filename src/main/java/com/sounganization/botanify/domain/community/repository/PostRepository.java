package com.sounganization.botanify.domain.community.repository;

import com.sounganization.botanify.domain.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    Page<Post> findAllByDeletedYnFalse(Pageable pageable);
    Page<Post> findAllByUserIdAndDeletedYnFalse(Long userId, Pageable pageable);

    List<Post> findAllByDeletedYnFalse();
}
