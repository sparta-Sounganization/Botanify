package com.sounganization.botanify.domain.community.service;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.req.PostUpdateReqDto;
import com.sounganization.botanify.domain.community.dto.res.CommentTempDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostWithCommentResDto;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.mapper.PostMapper;
import com.sounganization.botanify.domain.community.repository.CommentRepository;
import com.sounganization.botanify.domain.community.repository.PostRepository;
import com.sounganization.botanify.domain.s3.service.S3Service;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.projection.UserProjection;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
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
    @Mock private PopularPostService popularPostService;
    @Mock private S3Service s3Service;

    // 종속 계층 - 조회수 어뷰징 관련
    @Mock private ViewHistoryRedisService viewHistoryRedisService;


    private PostReqDto postReqDto;
    private Long userId;
    private Post post;
    private Post savedPost;
    private final List<Comment> replies = new ArrayList<>();
    private final List<Comment> thread = new ArrayList<>();
    private final List<Long> userIds = new ArrayList<>();
    private final List<UserProjection> userProjections = new ArrayList<>();
    private final List<CommentTempDto> commentDtos = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postReqDto = new PostReqDto("test title", "test content", null);
        userId = 1L;
        post = mock(Post.class);
        savedPost = Post.builder().id(1L).build();

        Comment childComment = Comment.builder().id(2L).build();
        replies.add(childComment);
        Comment parentComment = Comment.builder().id(1L).userId(1L).childComments(replies).build();
        thread.add(parentComment);

        userIds.add(userId);
        userProjections.add(new UserProjection() {
            @Override
            public Long getId() {
                return userId;
            }

            @Override
            public String getUsername() {
                return "test user";
            }
        });

        commentDtos.add(new CommentTempDto(1L,userId,"test user",null));
    }

    @Test
    void createPost_Success() {
        // given
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
        // given
        Long postId = 1L;
        Long userId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        doNothing().when(popularPostService).updatePostScore(postId);
        when(commentRepository.findCommentsByPostId(postId)).thenReturn(thread);
        when(userRepository.findUsernamesByIds(userIds)).thenReturn(userProjections);
        when(postMapper.entityToResDto(eq(post), eq(commentDtos)))
                .thenReturn(mock(PostWithCommentResDto.class));
        when(viewHistoryRedisService.isViewHistoryExist(anyLong(),anyLong(),any(LocalDate.class)))
                .thenReturn(false);

        // when
        PostWithCommentResDto result = postService.readPost(postId, userId);
        // then
        assertNotNull(result);
        verify(postRepository).findById(postId);
        verify(popularPostService).updatePostScore(postId);
        verify(commentRepository).findCommentsByPostId(postId);
        verify(userRepository).findUsernamesByIds(userIds);
        verify(postMapper).entityToResDto(eq(post), eq(commentDtos));
    }

    @Test
    void updatePost_Success() {
        // given
        Long postId = 1L;
        Long userId = 1L;
        PostUpdateReqDto updateReqDto = new PostUpdateReqDto("test title", "test content", null);
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
        doNothing().when(s3Service).deleteImage(any(String.class));

        // when
        postService.deletePost(postId, userId);

        // then
        assertEquals(Boolean.TRUE, comments.get(0).getDeletedYn());
        verify(postRepository).findById(postId);
        verify(commentRepository).findCommentsByPostId(postId);
    }

}