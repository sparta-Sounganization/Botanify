package com.sounganization.botanify.domain.terrarium.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record PlantPostRequest(
        @NotBlank
        String plantName,

        @NotNull @Positive
        Long speciesId,

        LocalDate adoptionDate
) {}
