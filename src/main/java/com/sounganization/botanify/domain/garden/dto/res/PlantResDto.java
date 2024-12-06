package com.sounganization.botanify.domain.garden.dto.res;

import lombok.Getter;

@Getter
public class PlantResDto {
    private int status;
    private String message;
    private long id;

    public PlantResDto(int status, String message, long id) {
        this.status = status;
        this.message = message;
        this.id = id;
    }
}