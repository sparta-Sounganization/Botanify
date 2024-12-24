package com.sounganization.botanify.domain.garden.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 제외하고 json 으로 변환
public record PlantResDto (
    Long id,
    String plantName,
    LocalDate adoptionDate,
    String speciesName,
    Page<DiaryResDto> diaries
) { }