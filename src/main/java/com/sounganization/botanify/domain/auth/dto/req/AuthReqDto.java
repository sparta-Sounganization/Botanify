package com.sounganization.botanify.domain.auth.dto.req;

import com.sounganization.botanify.domain.user.enums.UserRole;

public record AuthReqDto(
        String email,
        String password,
        String passwordCheck,
        String username,
        String city,
        String town,
        String address,
        UserRole role
) {}
