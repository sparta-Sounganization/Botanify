package com.sounganization.botanify.domain.garden.mapper;

import com.sounganization.botanify.domain.garden.dto.req.PlantReqDto;
import com.sounganization.botanify.domain.garden.entity.Plant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface PlantMapper {
    default Plant toEntity(PlantReqDto reqDto) {
        return Plant.builder()
                .plantName(reqDto.plantName())
                .adoptionDate(reqDto.adoptionDate())
                .build();
    }

}
