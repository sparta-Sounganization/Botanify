package com.sounganization.botanify.domain.community.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResDto {
    private String message;
    private Long id;
}
