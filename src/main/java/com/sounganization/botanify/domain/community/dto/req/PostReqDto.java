package com.sounganization.botanify.domain.community.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class PostReqDto {

    @NotBlank(message = "게시글 제목은 필수 입력입니다")
    @Length(max = 100, message = "게시글 제목은 100자 이하로 입력해야 합니다")
    private String title;

    @NotBlank(message = "게시글 내용은 필수 입력입니다")
    private String content;

}
