package com.sounganization.botanify.domain.community.dto.res;

import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public record PostWithCommentResDto (
    String title,
    String content,
    Integer viewCounts,
    List<CommentTempDto> comments
) {
    public static PostWithCommentResDto from(Post post, List<Comment> comments, Map<Long, String> usernameMap) {
        return PostWithCommentResDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .viewCounts(post.getViewCounts())
                .comments(comments.stream()
                        .map(comment -> {
                            //username 을 찾고, CommentTempDto 에 포함
                            String username = usernameMap.getOrDefault(comment.getUserId(), "알수없음");
                            return CommentTempDto.from(comment, username);
                        })
                        .collect(Collectors.toList()))
                .build();
    }
}
