package com.sounganization.botanify.domain.auth.dto.req;

import com.sounganization.botanify.domain.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupReqDto(
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\$%\\^&\\*])[A-Za-z\\d!@#\\$%\\^&\\*]{8,}$",
                message = "비밀번호는 최소 8자리 이상, 영문 대소문자, 숫자, 특수문자가 각각 1개 이상 포함되어야 합니다."
        )
        String password,
        @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\$%\\^&\\*])[A-Za-z\\d!@#\\$%\\^&\\*]{8,}$",
                message = "비밀번호는 최소 8자리 이상, 영문 대소문자, 숫자, 특수문자가 각각 1개 이상 포함되어야 합니다."
        )
        String passwordCheck,
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        String username,
        @NotBlank(message = "도시는 필수 입력 값입니다.")
        String city,
        @NotBlank(message = "시/군/구는 필수 입력 값입니다.")
        String town,
        @NotBlank(message = "상세 주소는 필수 입력 값입니다.")
        String address,
        UserRole role
) {}
