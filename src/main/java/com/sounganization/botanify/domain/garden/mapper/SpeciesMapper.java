package com.sounganization.botanify.domain.garden.mapper;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.domain.garden.dto.req.SpeciesReqDto;
import com.sounganization.botanify.domain.garden.dto.res.SpeciesResDto;
import com.sounganization.botanify.domain.garden.entity.Species;
import com.sounganization.botanify.domain.plantApi.dto.res.PlantApiResDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.HttpStatus;

@Mapper(componentModel = "spring")
public interface SpeciesMapper {
    default Species toEntity(SpeciesReqDto req) {
        return Species.builder()
                .plantName(req.plantName())
                .build();
    }

    default SpeciesResDto toDto(Species species) {
        return new SpeciesResDto(
                species.getId(),
                species.getPlantName()
        );
    }

    default CommonResDto toCreatedDto(Long createdId) {
        return new CommonResDto(
                HttpStatus.CREATED,
                "식물 품종이 추가되었습니다.",
                createdId
        );
    }

    default CommonResDto toUpdatedDto(Long updatedId) {
        return new CommonResDto(
                HttpStatus.OK,
                "식물 품종이 수정되었습니다.",
                updatedId
        );
    }

    @Mapping(source = "cntntsSj", target = "plantName")
    @Mapping(source = "codeNm", target = "speciesName")
    @Mapping(source = "cntntsNo", target = "plantCode")
    @Mapping(target = "id", ignore = true)
    Species toEntity(PlantApiResDto apiRes);
}
