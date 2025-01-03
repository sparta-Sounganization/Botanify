package com.sounganization.botanify.domain.community.service;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.community.dto.req.CommentReqDto;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.mapper.CommentMapper;
import com.sounganization.botanify.domain.community.repository.CommentRepository;
import com.sounganization.botanify.domain.community.repository.PostRepository;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.enums.UserRole;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static com.sounganization.botanify.domain.community.entity.QComment.comment;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PopularPostService popularPostService ;
    @Mock
    private Post post;

    private User user;

    @BeforeEach
    void setUp() {
        // User 객체와 관련된 값 초기화
        user = User.builder()
                .email("test@email.com")
                .username("이름")
                .password("abc1234!!!")
                .role(UserRole.USER)
                .city("서울시")
                .town("용산구 한강대로 405")
                .address("서울역")
                .build();
    }

    @Test
    void createComment_success() {
        //given
        Long postId = 1L;
        Long userId = 1L;
        CommentReqDto requestDto = new CommentReqDto("좋은 글입니다!");

        Comment savedComment = Comment.builder()
                .id(1L)
                .userId(userId)
                .depth(0)
                .post(post)
                .content("좋은 글입니다!")
                .build();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"좋은 글입니다!");  // 예상되는 반환 값

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(post.isDeletedYn()).thenReturn(false);
        when(commentMapper.toEntity(requestDto, post, userId, null)).thenReturn(savedComment);
        when(commentRepository.save(savedComment)).thenReturn(savedComment);
        when(commentMapper.toResDto(savedComment)).thenReturn(commonResDto );
        Mockito.doNothing().when(popularPostService).updatePostScore(postId);

        // when
        CommonResDto result = commentService.createComment(postId, requestDto, userId);

        // then
        verify(userRepository).findById(userId);
        verify(postRepository).findById(postId);
        verify(post).isDeletedYn();
        verify(commentMapper).toEntity(requestDto, post, userId, null);
        verify(commentRepository).save(savedComment);
        verify(popularPostService).updatePostScore(postId);

        assertNotNull(result);
        assertEquals("좋은 글입니다!", result.message());
    }

    @Test
    void createComment_userNotFound() {
        //given
        Long postId = 1L;
        Long userId = 1L;
        CommentReqDto requestDto = new CommentReqDto("좋은 글입니다!");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //when & then
        CustomException thrownException = Assertions.assertThrows(CustomException.class, () -> commentService.createComment(postId, requestDto, userId));
        assertEquals(ExceptionStatus.USER_NOT_FOUND, thrownException.getStatus());
    }

    @Test
    void createComment_postNotFound() {
        //given
        Long postId = 1L;
        Long userId = 1L;
        CommentReqDto requestDto = new CommentReqDto("좋은 글입니다!");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //when & then
        CustomException thrownException = Assertions.assertThrows(CustomException.class, () -> commentService.createComment(postId, requestDto, userId));
        assertEquals(ExceptionStatus.POST_NOT_FOUND, thrownException.getStatus());
    }

    @Test
    void createComment_postAlreadyDeleted() {
        //given
        Long postId = 1L;
        Long userId = 1L;
        CommentReqDto requestDto = new CommentReqDto("좋은 글입니다!");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(post.isDeletedYn()).thenReturn(true);

        // when & then
        CustomException thrownException = Assertions.assertThrows(CustomException.class, () -> commentService.createComment(postId, requestDto, userId));
        assertEquals(ExceptionStatus.POST_ALREADY_DELETED, thrownException.getStatus());
    }

    @Test
    void createReply() {
        //given
        Long userId = 1L;
        Long parentCommentId = 2L;
        CommentReqDto requestDto = new CommentReqDto("자식 댓글");
        Comment parentComment = Comment.builder()
                .id(parentCommentId)
                .content("부모 댓글")
                .depth(0)
                .post(post)
                .build();

        Comment reply = Comment.builder()
                .id(3L)
                .content(requestDto.content())
                .depth(1)
                .post(post)
                .build();
        CommonResDto expectedResponse = new CommonResDto(HttpStatus.CREATED, "댓글이 추가되었습니다.", reply.getId());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        when(commentMapper.toEntity(requestDto, post, userId, parentComment)).thenReturn(reply);
        when(commentRepository.save(reply)).thenReturn(reply);
        when(commentMapper.toResDto(reply)).thenReturn(expectedResponse);

        //when
        CommonResDto result = commentService.createReply(parentCommentId, requestDto, userId);

        assertEquals(reply.getId(), result.id());
        assertEquals(reply.getContent(), requestDto.content());
        verify(popularPostService).updatePostScore(post.getId());
    }


    @Test
    void updateComment() {
        //given
        Long commentId = 1L;
        Long userId = 1L;
        CommentReqDto commentReqDto = new CommentReqDto("댓글 수정 내용");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Comment comment = mock(Comment.class);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(comment.getUserId()).thenReturn(userId);
        when(comment.getPost()).thenReturn(post);
        when(post.getId()).thenReturn(1L);

        CommonResDto mockResponse = new CommonResDto(HttpStatus.OK, "댓글이 수정되었습니다.", commentId);
        when(commentMapper.toUpdateResDto(comment)).thenReturn(mockResponse);
        //when
        doNothing().when(popularPostService).updatePostScore(1L);
        CommonResDto response = commentService.updateComment(commentId, commentReqDto, userId);
        //then
        assertNotNull(response);
        assertEquals("댓글이 수정되었습니다.", response.message());
    }

    @Test
    void deleteComment() {
        //given
        Long commentId = 1L;
        Long userId = 1L;
        Comment comment = Comment.builder()
                .id(1L)
                .content("테스트댓글")
                .userId(1L)
                .depth(0)
                .post(post)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findCommentsById(commentId)).thenReturn(Optional.of(comment));
        //when
        commentService.deleteComment(commentId, userId);
        //then
        assertEquals("테스트댓글", comment.getContent());
        verify(popularPostService).updatePostScore(post.getId());
    }
}
