package com.sounganization.botanify.domain.community.dto.req;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

public record PostReqDto (
    @NotBlank(message = "게시글 제목은 필수 입력입니다")
    @Length(max = 100, message = "게시글 제목은 100자 이하로 입력해야 합니다")
    String title,

    @NotBlank(message = "게시글 내용은 필수 입력입니다")
    String content,

    @URL(message = "이미지 URL 형식이 올바르지 않습니다")
    String imageUrl
) {}
