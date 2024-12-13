package com.sounganization.botanify.domain.community.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommentTempDto {
    private Long commentId;
    private Long userId;
    private String username;
    private String content;
    private List<CommentTempDto> replies;

    public CommentTempDto(Long commentId, Long userId, String username, String content) {
        this.commentId = commentId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.replies = new ArrayList<>();
    }
}
