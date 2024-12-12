package com.sounganization.botanify.domain.garden.mapper;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.domain.garden.dto.req.DiaryReqDto;
import com.sounganization.botanify.domain.garden.dto.res.DiaryResDto;
import com.sounganization.botanify.domain.garden.entity.Diary;
import org.mapstruct.Mapper;
import org.springframework.http.HttpStatus;

@Mapper(componentModel = "Spring")
public interface DiaryMapper {
    default Diary toEntity(DiaryReqDto req) {
        return Diary.builder()
                .title(req.title())
                .content(req.content())
                .build();
    }

    default DiaryResDto toDto(Diary diary) {
        return new DiaryResDto(
                diary.getId(),
                diary.getTitle(),
                diary.getContent(),
                diary.getCreatedAt(),
                diary.getUpdatedAt()
        );
    }

    default CommonResDto toCreatedDto(Long createdId) {
        return new CommonResDto(
                HttpStatus.CREATED,
                "성장 일지가 추가되었습니다.",
                createdId
        );
    }

    default CommonResDto toUpdatedDto(Long updatedId) {
        return new CommonResDto(
                HttpStatus.OK,
                "성장 일지가 수정되었습니다.",
                updatedId
        );
    }
}
