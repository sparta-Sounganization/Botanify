package com.sounganization.botanify.domain.garden.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class PlantRegisterReqDto {
        @NotBlank(message = "공백일 수 없습니다.")
        private String plantName;

        @NotNull @Positive
        private Long speciesId;

        private LocalDate adoptionDate;
}
