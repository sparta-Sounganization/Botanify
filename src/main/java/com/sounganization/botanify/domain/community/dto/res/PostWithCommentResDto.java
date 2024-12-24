package com.sounganization.botanify.domain.community.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import java.util.List;

@Builder
public record PostWithCommentResDto (
    String title,
    String content,
    Integer viewCounts,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String imageUrl,
    List<CommentTempDto> comments
) { }
