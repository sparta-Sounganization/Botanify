package com.sounganization.botanify.domain.user.dto.req;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateReqDto(
        @NotBlank
        String password,
        @NotBlank
        String username,
        @NotBlank
        String newPassword,
        @NotBlank
        String city,
        @NotBlank
        String town,
        @NotBlank
        String address
) {}
