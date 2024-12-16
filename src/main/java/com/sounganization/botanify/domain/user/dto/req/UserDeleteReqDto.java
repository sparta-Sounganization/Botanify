package com.sounganization.botanify.domain.user.dto.req;

import jakarta.validation.constraints.NotBlank;

public record UserDeleteReqDto(
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {}
