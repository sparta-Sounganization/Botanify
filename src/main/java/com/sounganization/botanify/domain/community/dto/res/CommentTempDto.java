package com.sounganization.botanify.domain.community.dto.res;

import com.sounganization.botanify.domain.community.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentTempDto {
    private Long commentId;
    private Long userId;
    private String username;
    private String content;
    private List<CommentTempDto> childComments;


    public static CommentTempDto from(Comment comment,String username) {
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
