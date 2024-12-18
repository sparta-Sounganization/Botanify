package com.sounganization.botanify.domain.s3.dto.res;

public record ImageUrlResDto(
        String uploadUrl,
        String imageUrl
) { }
