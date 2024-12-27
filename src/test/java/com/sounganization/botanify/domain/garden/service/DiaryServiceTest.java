package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.domain.garden.dto.req.DiaryReqDto;
import com.sounganization.botanify.domain.garden.dto.res.DiaryResDto;
import com.sounganization.botanify.domain.garden.entity.Diary;
import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.entity.Species;
import com.sounganization.botanify.domain.garden.mapper.DiaryMapper;
import com.sounganization.botanify.domain.garden.repository.DiaryRepository;
import com.sounganization.botanify.domain.garden.repository.PlantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiaryServiceTest {
    @InjectMocks
    private DiaryService diaryService;

    //Mock
    @Mock private DiaryRepository diaryRepository;
    @Mock private PlantRepository plantRepository;
    @Mock private DiaryMapper diaryMapper;

    Long userId;
    Plant plant;
    DiaryReqDto diaryReqDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        diaryReqDto = new DiaryReqDto("test diary title", "test diary content");
        plant = Plant.builder().id(1L).userId(userId).species(Species.builder().id(1L).build()).build();

    }

    @Test
    void createDiary_Success() {
        // given
        Long plantId = 1L;
        Diary reqDiary = Diary.builder().id(1L).build();
        Diary resDiary = Diary.builder().id(1L).build();

        // Mock 설정
        when(plantRepository.findByIdCustom(plantId)).thenReturn(plant); // Plant 반환
        when(diaryMapper.toEntity(diaryReqDto)).thenReturn(reqDiary); // DiaryReqDto -> Diary 변환
        when(diaryRepository.save(reqDiary)).thenReturn(resDiary); // 저장된 Diary 반환
        when(diaryMapper.toCreatedDto(resDiary.getId()))
                .thenReturn(new CommonResDto(HttpStatus.OK, "success", resDiary.getId()));

        // when
        CommonResDto result = diaryService.createDiary(userId, plantId, diaryReqDto);

        // then
        assertNotNull(result); // 반환 값 검증
        verify(plantRepository).findByIdCustom(plantId); // Plant 조회 호출 검증
        verify(diaryMapper).toEntity(diaryReqDto); // Diary 변환 호출 검증
        verify(diaryRepository).save(reqDiary); // Diary 저장 호출 검증
        verify(diaryMapper).toCreatedDto(resDiary.getId()); // Response 변환 호출 검증
    }


    @Test
    void readDiary_Success() {
        // given
        Long id = 1L;
        Long userId = 1L;
        String title = "test diary title";
        String content = "test diary content";
        LocalDateTime createdAt = LocalDateTime.MIN;
        LocalDateTime updatedAt = LocalDateTime.MIN;

        // Diary 객체 생성
        Diary diary = Diary.builder().id(id).userId(userId).build();

        // Mock 설정
        when(diaryRepository.findByIdCustom(id)).thenReturn(diary); // findByIdCustom Mock
        when(diaryMapper.toDto(diary)).thenReturn(new DiaryResDto(id, title, content, createdAt, updatedAt)); // Diary -> DTO 변환 Mock

        // when
        DiaryResDto result = diaryService.readDiary(userId, id);

        // then
        assertNotNull(result); // 반환 값 검증
        verify(diaryRepository).findByIdCustom(id); // findByIdCustom 호출 검증
        verify(diaryMapper).toDto(diary); // Diary -> DTO 변환 호출 검증
    }


    @Test
    void updateDiary_Success() {
        // given
        Long id = 1L;
        DiaryReqDto diaryReqDto = new DiaryReqDto("test diary title", "test diary content");
        Diary diary = Diary.builder().id(id).userId(userId).build();
        // Mock
        when(diaryRepository.findByIdCustom(id)).thenReturn(diary);
        when(diaryMapper.toUpdatedDto(id)).thenReturn(new CommonResDto(HttpStatus.OK, "success", id));
        when(diaryRepository.findByIdCustom(id)).thenReturn(diary); // findByIdCustom Mock

        // when
        CommonResDto result = diaryService.updateDiary(userId, id, diaryReqDto);

        // then
        assertNotNull(result); // 반환 값 검증
        verify(diaryRepository).findByIdCustom(id); // findByIdCustom 호출 검증
        verify(diaryMapper).toUpdatedDto(id); // Response 변환 호출 검증
    }

    @Test
    void deleteDiary_Success() {
        // given
        Long id = 1L;
        Diary diary = Diary.builder().id(id).userId(userId).build();
        // Mock
        when(diaryRepository.findByIdCustom(id)).thenReturn(diary);

        // when
        diaryService.deleteDiary(userId, id);

        // then
        assertEquals(Boolean.TRUE, diary.getDeletedYn()); // 삭제 여부 확인
        verify(diaryRepository).findByIdCustom(id); // findByIdCustom 호출 검증
    }

}
