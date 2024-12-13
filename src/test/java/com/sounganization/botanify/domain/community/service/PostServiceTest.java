package com.sounganization.botanify.domain.community.service;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.req.PostUpdateReqDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.mapper.PostMapper;
import com.sounganization.botanify.domain.community.repository.CommentRepository;
import com.sounganization.botanify.domain.community.repository.PostRepository;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class PostServiceTest {

    // 테스트 대상
    @InjectMocks
    private PostService postService;

    // 종속 계층
    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private UserRepository userRepository;
    @Mock private PostMapper postMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPost_Success() {
        // given
        PostReqDto postReqDto = new PostReqDto("test title", "test content");
        Long userId = 1L;
        Post post = new Post();
        Post savedPost = Post.builder().id(1L).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(postMapper.reqDtoToEntity(postReqDto, userId)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(savedPost);
        when(postMapper.entityToResDto(savedPost, HttpStatus.CREATED)).thenReturn(mock(CommonResDto.class));

        // when
        CommonResDto result = postService.createPost(postReqDto, userId);

        // then
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(postMapper).reqDtoToEntity(postReqDto, userId);
        verify(postRepository).save(post);
        verify(postMapper).entityToResDto(savedPost, HttpStatus.CREATED);
    }

    @Test
    void readPosts_Success() {
        // given
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        List<Post> postList = new ArrayList<>();
        postList.add(Post.builder().id(1L).build());
        Page<Post> postPage = new PageImpl<>(postList, pageable, size);
        when(postRepository.findAllByDeletedYnFalse(any(Pageable.class))).thenReturn(postPage);
        when(postMapper.entityToResDto(any(Post.class))).thenReturn(mock(PostListResDto.class));

        // when
        Page<PostListResDto> result = postService.readPosts(page, size);

        // then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(postRepository).findAllByDeletedYnFalse(pageable);
        verify(postMapper).entityToResDto(postList.get(0));
    }

    @Test
    void readPost_Success() {
        // todo - 작성 중
        // given
        Long postId = 1L;
        // when
        postService.readPost(postId);
        // then

    }

    @Test
    void updatePost_Success() {
        // given
        Long postId = 1L;
        Long userId = 1L;
        PostUpdateReqDto updateReqDto = new PostUpdateReqDto("test title", "test content");
        Post post = Post.builder().userId(userId).build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.entityToResDto(post, HttpStatus.OK)).thenReturn(mock(CommonResDto.class));

        // when
        CommonResDto result = postService.updatePost(postId, updateReqDto, userId);

        // then
        assertNotNull(result);
        verify(postRepository).findById(postId);
        verify(postRepository).save(post);
        verify(postMapper).entityToResDto(post, HttpStatus.OK);
    }

    @Test
    void deletePost_Success() {
        // given
        Long postId = 1L;
        Long userId = 1L;
        Post post = Post.builder().userId(userId).build();
        List<Comment> comments = new ArrayList<>();
        comments.add(Comment.builder().id(1L).build());
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findCommentsByPostId(postId)).thenReturn(comments);

        // when
        postService.deletePost(postId, userId);

        // then
        assertEquals(Boolean.TRUE, comments.get(0).getDeletedYn());
        verify(postRepository).findById(postId);
        verify(commentRepository).findCommentsByPostId(postId);
    }

}