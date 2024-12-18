package com.sounganization.botanify.domain.community.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public record PostListResDto (
    Long id,
    String title,
    String content,
    Integer viewCounts,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String imageUrl
) {}
