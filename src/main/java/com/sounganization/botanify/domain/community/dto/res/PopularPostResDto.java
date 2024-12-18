package com.sounganization.botanify.domain.community.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;

public record PopularPostResDto(
        Long postId,
        String title,
        Integer viewCount,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String imageUrl,
        Integer commentCount,
        Double score
) {}