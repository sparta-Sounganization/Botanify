package com.sounganization.botanify.domain.community.dto.req;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class PostUpdateReqDto {
    @Length(max = 100, message = "게시글 제목은 100자 이하로 입력해야 합니다")
    private String title;
    private String content;
}
