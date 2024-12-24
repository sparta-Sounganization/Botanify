package com.sounganization.botanify.domain.garden.mapper;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.domain.garden.dto.req.SpeciesReqDto;
import com.sounganization.botanify.domain.garden.dto.res.SpeciesResDto;
import com.sounganization.botanify.domain.garden.entity.Species;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class SpeciesMapper {
    public Species toEntity(SpeciesReqDto req) {
        return Species.builder()
                .speciesName(req.speciesName())
                .description(req.description())
                .build();
    }

    public SpeciesResDto toDto(Species species) {
        return new SpeciesResDto(
                species.getId(),
                species.getSpeciesName(),
                species.getDescription()
        );
    }

    public CommonResDto toCreatedDto(Long createdId) {
        return new CommonResDto(
                HttpStatus.CREATED,
                "식물 품종이 추가되었습니다.",
                createdId
        );
    }

    public CommonResDto toUpdatedDto(Long updatedId) {
        return new CommonResDto(
                HttpStatus.OK,
                "식물 품종이 수정되었습니다.",
                updatedId
        );
    }
}
