package com.sounganization.botanify.domain.garden.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record PlantReqDto (
        @NotBlank(message = "공백일 수 없습니다.")
        String plantName,

        @NotNull @Positive
        Long speciesId,

        // todo - yyyy-MM-dd 형식이 LocalDate로 잘 변환되는지 테스트 후 주석 삭제
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜는 yyyy-MM-dd 형식이어야 합니다.")
        LocalDate adoptionDate
) { }
