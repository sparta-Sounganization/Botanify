package com.sounganization.botanify.domain.community.repository;

import com.sounganization.botanify.common.config.QueryDslConfig;
import com.sounganization.botanify.domain.community.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    private Post post;
    private Post postAlt;

    @BeforeEach
    @Transactional
    void setUp() {
        post = Post.builder()
                .title("test title")
                .content("test content")
                .viewCounts(1).userId(1L).build();
        postAlt = Post.builder()
                .title("")
                .content("")
                .viewCounts(0).userId(2L).build();
        Post deletedPost = Post.builder()
                .title("test title")
                .content("test content")
                .viewCounts(1).userId(1L).build();
        deletedPost.softDelete();

        postRepository.save(post);
        postRepository.save(postAlt);
        postRepository.save(deletedPost);
    }

    @Test
    @Transactional
    void findAllByDeletedYnFalse_Success() {
        // given
        Pageable pageable = PageRequest.of(0,10);
        // when
        Page<Post> result = postRepository.findAllByDeletedYnFalse(pageable);
        // then
        assertNotNull(result);
        assertEquals(2L, result.getTotalElements());
        assertTrue(result.getContent().containsAll(List.of(post,postAlt)));
        assertTrue(result.getContent().stream().noneMatch(Post::isDeletedYn));
    }

    @Test
    @Transactional
    void findAllByUserIdAndDeletedYnFalse_Success() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0,10);
        // when
        Page<Post> result = postRepository.findAllByUserIdAndDeletedYnFalse(userId, pageable);
        // then
        assertNotNull(result);
        assertEquals(1L, result.getTotalElements());
        assertTrue(result.getContent().contains(post));
        assertTrue(result.getContent().stream().noneMatch(Post::isDeletedYn));
    }
}