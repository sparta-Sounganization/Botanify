package com.sounganization.botanify.domain.user.dto.res;

import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import org.springframework.data.domain.Page;

public record UserPostsResDto(
        UserResDto userInfo,
        Page<PostListResDto> posts) {}
