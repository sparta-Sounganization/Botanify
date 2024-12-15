package com.sounganization.botanify.domain.auth.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SigninReqDto (
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\$%\\^&\\*])[A-Za-z\\d!@#\\$%\\^&\\*]{8,}$",
                message = "비밀번호는 최소 8자리 이상, 영문 대소문자, 숫자, 특수문자가 각각 1개 이상 포함되어야 합니다."
        )
        String password)
{}
