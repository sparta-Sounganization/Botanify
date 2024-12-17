package com.sounganization.botanify.domain.community.dto.res;

public record PopularPostResDto(
        Long postId,
        String title,
        Integer viewCount,
        Integer commentCount,
        Double score
) {}