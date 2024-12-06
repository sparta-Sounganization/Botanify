package com.sounganization.botanify.domain.garden.mapper;

import com.sounganization.botanify.domain.garden.dto.req.DiaryReqDto;
import com.sounganization.botanify.domain.garden.dto.res.MessageResDto;
import com.sounganization.botanify.domain.garden.dto.res.DiaryResDto;
import com.sounganization.botanify.domain.garden.entity.Diary;
import org.springframework.http.HttpStatus;

public class DiaryMapper {
    public static Diary toEntity(DiaryReqDto req) {
        return Diary.builder()
                .title(req.title())
                .content(req.content())
                .build();
    }

    public static DiaryResDto toDto(Diary diary) {
        return new DiaryResDto(
                diary.getId(),
                diary.getTitle(),
                diary.getContent(),
                diary.getCreatedAt(),
                diary.getUpdatedAt()
        );
    }

    // todo - 응답 형식이 도메인마다 중복되므로, AcceptedResDto,CommonResDto 등으로 리팩토링 제안할 것

    public static MessageResDto toCreatedDto(Long createdId) {
        return new MessageResDto(
                HttpStatus.CREATED.value(),
                "성장 일지가 추가되었습니다.",
                createdId
        );
    }

    public static MessageResDto toUpdatedDto(Long updatedId) {
        return new MessageResDto(
                HttpStatus.OK.value(),
                "성장 일지가 수정되었습니다.",
                updatedId
        );
    }
}
