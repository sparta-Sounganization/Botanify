package com.sounganization.botanify.domain.community.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sounganization.botanify.domain.community.entity.Comment;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CommentTempDto (
    Long commentId,
    Long userId,
    String username,
    String content,
    List<CommentTempDto> childComments
) {

    public static CommentTempDto from(Comment comment, String username) {
        return CommentTempDto.builder()
                .commentId(comment.getId())
                .userId(comment.getUserId())
                .username(username)
                .content(comment.getContent())
                .childComments(comment.getChildComments().stream()
                        .map(child -> from(child, username))
                        .collect(Collectors.toList()))
                .build();
    }
}
