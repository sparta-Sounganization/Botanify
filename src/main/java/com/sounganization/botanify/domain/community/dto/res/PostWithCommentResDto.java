package com.sounganization.botanify.domain.community.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostWithCommentResDto {
    private String title;
    private String content;
    private Integer viewCounts;
    private List<CommentTempDto> comments;
}
