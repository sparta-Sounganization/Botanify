package com.sounganization.botanify.domain.community.dto.req;

import org.hibernate.validator.constraints.Length;

public record PostUpdateReqDto (
        @Length(max = 100, message = "게시글 제목은 100자 이하로 입력해야 합니다")
        String title,
        String content
) {}
