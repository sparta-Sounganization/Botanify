package com.sounganization.botanify.domain.plantApi.dto.res;

import lombok.Builder;

@Builder
public record CategoryResDto(
        String CategoryCode,
        String CategoryName
) {}
