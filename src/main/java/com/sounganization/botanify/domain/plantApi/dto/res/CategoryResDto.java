package com.sounganization.botanify.domain.plantApi.dto.res;

import lombok.Builder;

@Builder
public record CategoryResDto(
        String categoryCode,
        String categoryName
) {}
