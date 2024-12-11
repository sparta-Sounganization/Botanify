package com.sounganization.botanify.domain.user.dto.req;

import com.sounganization.botanify.domain.user.enums.UserRole;

public record UserReqDto(
        Long id,
        String email,
        String username,
        String password,
        String city,
        String town,
        String address,
        UserRole role
) {}
