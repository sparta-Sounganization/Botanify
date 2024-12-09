package com.sounganization.botanify.domain.garden.dto.res;

import com.sounganization.botanify.domain.garden.entity.Species;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PlantResDto {
    private int status;
    private String message;
    private long id;
    private String plantName;
    private LocalDate adoptionDate;
    private String speciesName;
    private List<DiaryResDto> diaries;

    public PlantResDto(int status, String message, long id, String plantName, LocalDate adoptionDate, String speciesName, List<DiaryResDto> diaries) {
        this.status = status;
        this.message = message;
        this.id = id;
        this.plantName = plantName;
        this.adoptionDate = adoptionDate;
        this.speciesName = speciesName;
        this.diaries = diaries;
    }

}