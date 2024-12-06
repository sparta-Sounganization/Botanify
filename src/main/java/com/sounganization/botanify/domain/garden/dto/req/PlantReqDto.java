package com.sounganization.botanify.domain.garden.dto.req;

import com.sounganization.botanify.domain.garden.entity.Species;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class PlantReqDto {
        @NotBlank(message = "공백일 수 없습니다.")
        private String plantName;

        @NotNull @Positive
        private Long speciesId;

        private LocalDate adoptionDate;
}
