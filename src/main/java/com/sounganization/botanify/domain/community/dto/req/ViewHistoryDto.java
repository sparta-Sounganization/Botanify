package com.sounganization.botanify.domain.community.dto.req;

import java.time.LocalDate;

public record ViewHistoryDto(
        Long postId,
        Long userId,
        LocalDate viewedAt
) {
}
