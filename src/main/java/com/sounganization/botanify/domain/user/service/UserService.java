package com.sounganization.botanify.domain.user.service;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.common.util.JwtUtil;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.mapper.PostMapper;
import com.sounganization.botanify.domain.community.repository.PostRepository;
import com.sounganization.botanify.domain.garden.dto.res.DiaryResDto;
import com.sounganization.botanify.domain.garden.entity.Diary;
import com.sounganization.botanify.domain.garden.mapper.DiaryMapper;
import com.sounganization.botanify.domain.garden.repository.DiaryRepository;
import com.sounganization.botanify.domain.user.dto.req.UserDeleteReqDto;
import com.sounganization.botanify.domain.user.dto.req.UserUpdateReqDto;
import com.sounganization.botanify.domain.user.dto.res.UserPlantsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserPostsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserResDto;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.mapper.UserMapper;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final DiaryRepository diaryRepository;
    private final DiaryMapper diaryMapper;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserResDto getUserInfo() {
        User user = getAuthenticatedUser();
        return userMapper.toResDto(user);
    }

    public UserPlantsResDto getUserInfoWithDiaries(int page, int size) {
        User user = getAuthenticatedUser();

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Diary> diaries = diaryRepository.findAllByPlantIdAndDeletedYnFalse(user.getId(), pageable);

        Page<DiaryResDto> diaryResDtos = diaries.map(diaryMapper::toDto);

        UserResDto userResDto = userMapper.toResDto(user);
        return new UserPlantsResDto(userResDto, diaryResDtos);
    }

    public UserPostsResDto getUserInfoWithPosts(int page, int size) {
        User user = getAuthenticatedUser();

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Post> posts = postRepository.findAll(pageable);

        Page<PostListResDto> postResDtos = posts
                .map(postMapper::entityToResDto);

        UserResDto userResDto = userMapper.toResDto(user);
        return new UserPostsResDto(userResDto, postResDtos);
    }

    @Transactional
    public CommonResDto updateUserInfo(UserUpdateReqDto updateReqDto) {
        User user = getAuthenticatedUser();

        if (!passwordEncoder.matches(updateReqDto.password(), user.toUserDetails().getPassword())) {
            throw new CustomException(ExceptionStatus.INVALID_PASSWORD);
        }

        String newPassword = updateReqDto.newPassword()
                != null ? passwordEncoder.encode(updateReqDto.newPassword()) : user.toUserDetails().getPassword();

        userRepository.updateUserInfo(user.getId(),
                updateReqDto.username(),
                newPassword,
                updateReqDto.city(),
                updateReqDto.town(),
                updateReqDto.address());

        return new CommonResDto(HttpStatus.OK, "회원정보가 수정되었습니다.", user.getId());
    }

    @Transactional
    public void deleteUser(UserDeleteReqDto userDeleteReqDto) {
        User user = getAuthenticatedUser();

        if (!passwordEncoder.matches(userDeleteReqDto.password(), user.toUserDetails().getPassword())) {
            throw new CustomException(ExceptionStatus.INVALID_PASSWORD);
        }

        user.softDelete();
        userRepository.save(user);
    }

    private User getAuthenticatedUser() {
        String userIdStr = jwtUtil.getCurrentUserId();
        Long userId = Long.parseLong(userIdStr);
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.USER_DETAILS_NOT_FOUND));
    }
}
