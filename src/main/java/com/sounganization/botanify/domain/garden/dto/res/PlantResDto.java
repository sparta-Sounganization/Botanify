package com.sounganization.botanify.domain.garden.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
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


}