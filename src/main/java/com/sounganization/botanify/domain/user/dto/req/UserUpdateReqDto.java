package com.sounganization.botanify.domain.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserUpdateReqDto(
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\$%\\^&\\*])[A-Za-z\\d!@#\\$%\\^&\\*]{8,}$",
                message = "비밀번호는 최소 8자리 이상, 영문 대소문자, 숫자, 특수문자가 각각 1개 이상 포함되어야 합니다."
        )
        String password,
        String username,
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\$%\\^&\\*])[A-Za-z\\d!@#\\$%\\^&\\*]{8,}$",
                message = "비밀번호는 최소 8자리 이상, 영문 대소문자, 숫자, 특수문자가 각각 1개 이상 포함되어야 합니다."
        )
        String newPassword,
        String city,
        String town,
        String address
) {}
