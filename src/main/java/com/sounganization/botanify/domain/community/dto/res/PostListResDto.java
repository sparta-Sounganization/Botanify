package com.sounganization.botanify.domain.community.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostListResDto {
    private final String title;
    private final String content;
    private final Integer viewCounts;
}
