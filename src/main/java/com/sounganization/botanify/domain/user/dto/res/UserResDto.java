package com.sounganization.botanify.domain.user.dto.res;

import com.sounganization.botanify.domain.user.enums.UserRole;

public record UserResDto (
        String username,
        UserRole role,
        String city,
        String town,
        String address) {}
