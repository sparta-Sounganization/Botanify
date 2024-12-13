package com.sounganization.botanify.domain.community.dto.res;

import lombok.Builder;
import java.util.List;

@Builder
public record PostWithCommentResDto (
    String title,
    String content,
    Integer viewCounts,
    List<CommentTempDto> comments
) { }
