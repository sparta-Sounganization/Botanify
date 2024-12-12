package com.sounganization.botanify.domain.garden.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record SpeciesReqDto(
        @NotBlank @Length(max = 50)
        String speciesName,

        @NotNull
        String description
) { }