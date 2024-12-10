package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.dto.req.DiaryReqDto;
import com.sounganization.botanify.domain.garden.dto.res.DiaryResDto;
import com.sounganization.botanify.domain.garden.dto.res.MessageResDto;
import com.sounganization.botanify.domain.garden.entity.Diary;
import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.mapper.DiaryMapper;
import com.sounganization.botanify.domain.garden.repository.DiaryRepository;
import com.sounganization.botanify.domain.garden.repository.PlantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final PlantRepository plantRepository;
    private final DiaryMapper diaryMapper;

    /**
     * 사용자 id, 식물 id와 저장하고자 하는 일지 DTO 를 받아서,
     * 사용자의 식물이 맞을 경우에만 일지를 등록하는 서비스 메서드
     * @param userId 사용자 id
     * @param plantId 식물 id
     * @param reqDto 저장하고자 하는 일지 DTO
     * @return 저장된 일지 엔티티
     */
    @Transactional
    public MessageResDto createDiary(Long userId, Long plantId, DiaryReqDto reqDto) {

        Diary reqDiary = diaryMapper.toEntity(reqDto);

        // todo - userId 에 대한 존재 확인
        if (Objects.isNull(userId)) throw new CustomException(ExceptionStatus.USER_NOT_FOUND);

        Plant plant = plantRepository.findByIdCustom(plantId);

        // userId 에 대한 plant 소유 여부 확인
        if (!Objects.equals(userId, plant.getUserId())) throw new CustomException(ExceptionStatus.PLANT_NOT_OWNED);

        // 별도로 취득한 연관 정보 추가
        reqDiary.addRelations(plant, userId);

        Diary resDiary = diaryRepository.save(reqDiary);

        return diaryMapper.toCreatedDto(resDiary.getId());
    }

    /**
     * 사용자 id와 불러오고자 하는 일지 id를 받아서,
     * 사용자의 일지가 맞을 경우에만 해당 일지를 반환하는 서비스 메서드
     * @param userId 사용자 id
     * @param id 불러오고자 하는 일지 id
     * @return 불러 온 일지 엔티티
     */
    public DiaryResDto readDiary(Long userId, Long id) {

        // todo - userId 에 대한 존재 확인
        if (Objects.isNull(userId)) throw new CustomException(ExceptionStatus.USER_NOT_FOUND);

        Diary diary = diaryRepository.findByIdCustom(id);

        // userId 에 대한 diary 소유 여부 확인
        if (!Objects.equals(userId, diary.getUserId())) throw new CustomException(ExceptionStatus.DIARY_NOT_OWNED);

        return diaryMapper.toDto(diary);
    }

    /**
     * 사용자 id와 수정하고자 하는 일지 id, 수정 사항이 담긴 일지 DTO 를 받아서,
     * 사용자의 일지가 맞을 경우에만 해당 일지를 수정하는 서비스 메서드
     * @param userId 사용자 id
     * @param id 수정하고자 하는 일지 id
     * @param reqDto 수정 사항이 담긴 일지 DTO
     * @return 수정된 일지 엔티티
     */
    @Transactional
    public MessageResDto updateDiary(Long userId, Long id, DiaryReqDto reqDto) {

        Diary reqDiary = diaryMapper.toEntity(reqDto);

        // todo - userId 에 대한 존재 확인
        if (Objects.isNull(userId)) throw new CustomException(ExceptionStatus.USER_NOT_FOUND);

        Diary diary = diaryRepository.findByIdCustom(id);

        // userId 에 대한 diary 소유 여부 확인
        if (!Objects.equals(userId, diary.getUserId())) throw new CustomException(ExceptionStatus.DIARY_NOT_OWNED);

        diary.update(reqDiary.getTitle(), reqDiary.getContent());

        return diaryMapper.toUpdatedDto(diary.getId());
    }

    /**
     * 사용자 id와 삭제하고자 하는 일지 id를 받아서,
     * 사용자의 일지가 맞을 경우에만 Soft Delete 를 통해 삭제하는 서비스 메서드
     * @param userId 사용자 id
     * @param id 삭제하고자 하는 일지 id
     */
    @Transactional
    public void deleteDiary(Long userId, Long id) {

        // todo - userId 에 대한 존재 확인
        if (Objects.isNull(userId)) throw new CustomException(ExceptionStatus.USER_NOT_FOUND);

        Diary diary = diaryRepository.findByIdCustom(id);

        // userId 에 대한 diary 소유 여부 확인
        if (!Objects.equals(userId, diary.getUserId())) throw new CustomException(ExceptionStatus.DIARY_NOT_OWNED);

        diary.softDelete();
    }
}
