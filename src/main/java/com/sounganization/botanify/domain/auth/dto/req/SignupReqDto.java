package com.sounganization.botanify.domain.auth.dto.req;

import com.sounganization.botanify.domain.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupReqDto(
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        String password,
        @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
        String passwordCheck,
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        String username,
        @NotBlank(message = "도시는 필수 입력 값입니다.")
        String city,
        @NotBlank(message = "동/읍/면은 필수 입력 값입니다.")
        String town,
        @NotBlank(message = "상세 주소는 필수 입력 값입니다.")
        String address,
        UserRole role
) {}
