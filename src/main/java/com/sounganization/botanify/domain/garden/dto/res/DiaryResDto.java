package com.sounganization.botanify.domain.garden.dto.res;

import java.time.LocalDateTime;

public record DiaryResDto(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
