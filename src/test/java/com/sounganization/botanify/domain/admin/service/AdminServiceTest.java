package com.sounganization.botanify.domain.admin.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.mapper.PostMapper;
import com.sounganization.botanify.domain.community.repository.PostRepository;
import com.sounganization.botanify.domain.user.dto.res.UserPostsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserResDto;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.enums.UserRole;
import com.sounganization.botanify.domain.user.mapper.UserMapper;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @InjectMocks
    private AdminService adminService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostRepository postRepository;

    private User user;
    private Long userId;
    private UserResDto userResDto;

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

        userResDto = new UserResDto("이름", UserRole.USER, "서울시", "용산구 한강대로 405", "서울역");

        userId = 1L; // userId 값 초기화
    }

    @Test
    @DisplayName("유저 프로필 조회 - 성공")
    void getUserProfile_Success() {
        // given
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toResDto(user)).thenReturn(userResDto);

        // when
        UserResDto result = adminService.getUserProfile(userId);

        // then
        Assertions.assertEquals(userResDto, result);
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userMapper).toResDto(user);
    }

    @Test
    @DisplayName("유저 프로필 조회 - 실패")
    void getUserProfile_Failure() {
        // given
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        CustomException thrownException = Assertions.assertThrows(CustomException.class, () -> adminService.getUserProfile(userId));
        Assertions.assertEquals(ExceptionStatus.USER_DETAILS_NOT_FOUND, thrownException.getStatus());
        Mockito.verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("유저 프로필 조회시 유저 게시글도 같이 - 성공")
    void getUserProfileWithPlants_Success() {
        // given
        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .content("This is a test post")
                .imageUrl("이미지URL")
                .build();

        PostListResDto postListResDto = new PostListResDto(post.getId(), post.getTitle(), post.getContent(), post.getViewCounts(), post.getImageUrl());
        Page<PostListResDto> postResDtos = new PageImpl<>(List.of(postListResDto));

        UserPostsResDto userPostsResDto = new UserPostsResDto(userResDto, postResDtos);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toResDto(user)).thenReturn(userResDto);
        Mockito.when(postRepository.findAllByUserIdAndDeletedYnFalse(user.getId(), PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(List.of(post)));
        Mockito.when(postMapper.entityToResDto(post)).thenReturn(postListResDto);

        // when
        UserPostsResDto result = adminService.getUserProfileWithPosts(userId, 1, 5);

        // then
        Assertions.assertEquals(userPostsResDto, result);
        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userMapper).toResDto(user);
        Mockito.verify(postRepository).findAllByUserIdAndDeletedYnFalse(user.getId(), PageRequest.of(0, 5));
        Mockito.verify(postMapper).entityToResDto(post);
    }
}
