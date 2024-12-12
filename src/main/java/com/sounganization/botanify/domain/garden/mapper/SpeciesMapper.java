package com.sounganization.botanify.domain.garden.mapper;

import com.sounganization.botanify.domain.garden.dto.req.SpeciesReqDto;
import com.sounganization.botanify.domain.garden.dto.res.MessageResDto;
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

    // todo - 응답 형식이 도메인마다 중복되므로, AcceptedResDto,CommonResDto 등으로 리팩토링 제안할 것

    public MessageResDto toCreatedDto(Long createdId) {
        return new MessageResDto(
                HttpStatus.CREATED.value(),
                "성장 일지가 추가되었습니다.",
                createdId
        );
    }

    public MessageResDto toUpdatedDto(Long updatedId) {
        return new MessageResDto(
                HttpStatus.OK.value(),
                "성장 일지가 수정되었습니다.",
                updatedId
        );
    }
}
