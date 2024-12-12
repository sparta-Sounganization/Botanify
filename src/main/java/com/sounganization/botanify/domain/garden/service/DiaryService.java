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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // plant 획득 및 소유권 검증
        Plant plant = plantRepository.findByIdCustom(plantId);
        if (!Objects.equals(userId, plant.getUserId())) throw new CustomException(ExceptionStatus.PLANT_NOT_OWNED);
        // 요청 DTO 를 diary 로 변환 후 연관 설정
        Diary reqDiary = diaryMapper.toEntity(reqDto);
        reqDiary.addRelations(plant, userId);
        // 저장 (영속화)
        Diary resDiary = diaryRepository.save(reqDiary);
        // id 전달하여 반환
        return diaryMapper.toCreatedDto(resDiary.getId());
    }

    /**
     * 사용자 id와 불러오고자 하는 일지 id를 받아서,
     * 사용자의 일지가 맞을 경우에만 해당 일지를 반환하는 서비스 메서드
     * @param userId 사용자 id
     * @param id 불러오고자 하는 일지 id
     * @return 불러 온 일지 엔티티
     */
    @Transactional(readOnly = true)
    public DiaryResDto readDiary(Long userId, Long id) {
        // diary 획득 및 소유권 검증
        Diary diary = findAuthoredDiaryPersist(userId, id);
        // DTO 로 변환하여 반환
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
        // diary 획득 및 소유권 검증 (영속화)
        Diary diary = findAuthoredDiaryPersist(userId, id);
        // 요청 Dto 에서 바로 diary 갱신
        diary.update(reqDto.title(), reqDto.content());
        // id 전달하여 반환
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
        // diary 획득 및 소유권 검증 (영속화)
        Diary diary = findAuthoredDiaryPersist(userId, id);
        // SoftDelete 전용 메서드를 이용한 삭제 필드 갱신
        diary.softDelete();
    }

    /**
     * 사용자 id와 일지 id를 받아서,
     * 사용자의 소유인 일지를 찾아서 반환하고, 그렇지 않은 경우 해당하는 예외를 던지는 서브루틴
     * @param userId 사용자 id
     * @param id 조회 및 소유권 확인하고자 하는 일지 id
     * @return 소유권이 확인된 존재하는 일지 엔티티
     */
    @Transactional
    protected Diary findAuthoredDiaryPersist(Long userId, Long id) {
        // diary 획득
        Diary diary = diaryRepository.findByIdCustom(id);
        // 소유권 검증
        if (!Objects.equals(userId, diary.getUserId())) throw new CustomException(ExceptionStatus.DIARY_NOT_OWNED);
        return diary;
    }
}
