package com.sounganization.botanify.domain.community.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    private Post post;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .title("test title")
                .content("test content")
                .viewCounts(0)
                .userId(1L)
                .build();
    }

    @Test
    void updatePost_Success() {
        // given
        String updatedTitle = "updated title";
        String updatedContent = "updated content";
        // when
        post.updatePost(updatedTitle, updatedContent);
        // then
        assertEquals(updatedTitle, post.getTitle());
        assertEquals(updatedContent, post.getContent());
    }

    @Test
    void updatePost_Fail() {
        // when
        post.updatePost(null, null);
        // then
        assertNotNull(post.getTitle());
        assertNotNull(post.getContent());
    }

    @Test
    void before_softDelete_isDeletedYn() {
        // when
        boolean result = post.isDeletedYn();
        // then
        assertFalse(result);
    }

    @Test
    void after_softDelete_isDeletedYn() {
        // given
        post.softDelete();
        // when
        boolean result = post.isDeletedYn();
        // then
        assertTrue(result);
    }
}