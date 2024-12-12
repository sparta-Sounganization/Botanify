package com.sounganization.botanify.domain.garden.dto.res;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

@Getter
@Builder
public class PlantResDto {
    private Long id;
    private String plantName;
    private LocalDate adoptionDate;
    private String speciesName;
    private Page<DiaryResDto> diaries;

    public PlantResDto(long id, String plantName, LocalDate adoptionDate, String speciesName, Page<DiaryResDto> diaries) {
        this.id = id;
        this.plantName = plantName;
        this.adoptionDate = adoptionDate;
        this.speciesName = speciesName;
        this.diaries = diaries;
    }


}