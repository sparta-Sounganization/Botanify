package com.sounganization.botanify.domain.garden.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record DiaryReqDto(
        @NotBlank @Length(max = 100)
        String title,

        @NotNull
        String content
) {}
