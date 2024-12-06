package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.entity.Diary;
import com.sounganization.botanify.domain.garden.entity.Plant;
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

    /**
     * 사용자 id, 식물 id와 저장하고자 하는 일지 엔티티를 받아서,
     * 사용자의 식물이 맞을 경우에만 일지를 등록하는 서비스 메서드
     * @param userId 사용자 id
     * @param plantId 식물 id
     * @param reqDiary 저장하고자 하는 일지 엔티티
     * @return 저장된 일지 엔티티
     */
    @Transactional
    public Diary createDiary(Long userId, Long plantId, Diary reqDiary) {

        // todo - userId 에 대한 존재 확인
        if (Objects.isNull(userId)) throw new CustomException(ExceptionStatus.USER_NOT_FOUND);

        // todo - userId 에 대한 plant 소유 여부 확인
        if (false) throw new CustomException(ExceptionStatus.PLANT_NOT_OWNED);

        Plant plant = plantRepository.findByIdCustom(plantId);

        reqDiary.setUserId(userId);
        reqDiary.setPlant(plant);

        return diaryRepository.save(reqDiary);
    }

    /**
     * 사용자 id와 불러오고자 하는 일지 id를 받아서,
     * 사용자의 일지가 맞을 경우에만 해당 일지를 반환하는 서비스 메서드
     * @param userId 사용자 id
     * @param id 불러오고자 하는 일지 id
     * @return 불러 온 일지 엔티티
     */
    public Diary readDiary(Long userId, Long id) {

        // todo - userId 에 대한 존재 확인
        if (Objects.isNull(userId)) throw new CustomException(ExceptionStatus.USER_NOT_FOUND);

        // todo - userId 에 대한 diary 소유 여부 확인
        if (false) throw new CustomException(ExceptionStatus.DIARY_NOT_OWNED);

        return diaryRepository.findByIdCustom(id);
    }

    /**
     * 사용자 id와 수정하고자 하는 일지 id, 수정 사항이 담긴 일지 엔티티를 받아서,
     * 사용자의 일지가 맞을 경우에만 해당 일지를 수정하는 서비스 메서드
     * @param userId 사용자 id
     * @param id 수정하고자 하는 일지 id
     * @param reqDiary 수정 사항이 담긴 일지 엔티티
     * @return 수정된 일지 엔티티
     */
    @Transactional
    public Diary updateDiary(Long userId, Long id, Diary reqDiary) {

        // todo - userId 에 대한 존재 확인
        if (Objects.isNull(userId)) throw new CustomException(ExceptionStatus.USER_NOT_FOUND);

        // todo - userId 에 대한 diary 소유 여부 확인
        if (false) throw new CustomException(ExceptionStatus.DIARY_NOT_OWNED);

        Diary diary = diaryRepository.findByIdCustom(id);

        // note - 일일이 필드 접근하는 것 마음에 들지 않음, 수정 방법 찾아보기
        diary.setTitle(reqDiary.getTitle());
        diary.setContent(reqDiary.getContent());

        return diaryRepository.save(diary);
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

        // todo - userId 에 대한 diary 소유 여부 확인
        if (false) throw new CustomException(ExceptionStatus.DIARY_NOT_OWNED);

        Diary diary = diaryRepository.findByIdCustom(id);

        diary.softDelete();
        diaryRepository.save(diary);
    }
}
