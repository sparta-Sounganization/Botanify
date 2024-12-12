package com.sounganization.botanify.domain.user.dto.res;

import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.garden.dto.res.DiaryResDto;
import org.springframework.data.domain.Page;

public record UserPostsResDto(
        UserResDto userInfo,
        Page<PostListResDto> posts) {}
