package com.sounganization.botanify.domain.community.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CommentTempDto (
    Long commentId,
    Long userId,
    String username,
    String content,
    List<CommentTempDto> replies
) {

    public CommentTempDto(Long commentId, Long userId, String username, String content) {
        this(commentId, userId, username, content, new ArrayList<>());
    }
}
