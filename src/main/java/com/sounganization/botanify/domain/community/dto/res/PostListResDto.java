package com.sounganization.botanify.domain.community.dto.res;

import lombok.Builder;

@Builder
public record PostListResDto (
    Long id,
    String title,
    String content,
    Integer viewCounts
) { }
