package com.sounganization.botanify.domain.garden.dto.res;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

@Builder
public record PlantResDto (
    Long id,
    String plantName,
    LocalDate adoptionDate,
    String speciesName,
    Page<DiaryResDto> diaries
) { }