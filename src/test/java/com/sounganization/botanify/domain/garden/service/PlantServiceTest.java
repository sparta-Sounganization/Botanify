package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.domain.garden.dto.req.PlantReqDto;
import com.sounganization.botanify.domain.garden.dto.res.DiaryResDto;
import com.sounganization.botanify.domain.garden.dto.res.PlantResDto;
import com.sounganization.botanify.domain.garden.entity.Diary;
import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.entity.Species;
import com.sounganization.botanify.domain.garden.mapper.DiaryMapper;
import com.sounganization.botanify.domain.garden.mapper.PlantMapper;
import com.sounganization.botanify.domain.garden.repository.DiaryRepository;
import com.sounganization.botanify.domain.garden.repository.PlantRepository;
import com.sounganization.botanify.domain.garden.repository.SpeciesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlantServiceTest {

    // 테스트 대상
    @InjectMocks
    private PlantService plantService;

    @Mock private PlantRepository plantRepository;
    @Mock private SpeciesRepository speciesRepository;
    @Mock private DiaryRepository diaryRepository;
    @Mock private PlantMapper plantMapper;
    @Mock private DiaryMapper diaryMapper;
    @Mock private PlantResDto plantResDto;

    Long userId;
    PlantReqDto plantReqDto;
    Plant plant;
    Plant savedPlant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        plantReqDto = new PlantReqDto("식물이름", 1L, LocalDate.MIN);
        plant = Plant.builder().id(1L).userId(userId).species(Species.builder().id(1L).build()).build();
        savedPlant = Plant.builder().id(1L).build();
    }

    @Test
    void createPlant_Success() {
        //given
        when(plantMapper.toEntity(any(PlantReqDto.class))).thenReturn(plant);
        when(plantRepository.save(any(Plant.class))).thenReturn(savedPlant);

        //when
        Long result = plantService.createPlant(userId, plantReqDto);

        //then
        assertNotNull(result);
        verify(plantMapper).toEntity(plantReqDto);
        verify(plantRepository).save(plant);
    }

    @Test
    void readPlant_Success() {
        // given
        Long plantId = 1L;
        int page = 1;
        int size = 10;

        Long diaryId = 1L;
        String diaryTitle = "일기 제목";
        String diaryContent = "일기 내용";
        LocalDateTime diaryCreatedAt = LocalDateTime.now();
        LocalDateTime diaryUpdatedAt = LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, size);
        when(plantRepository.findByIdCustom(any(Long.class))).thenReturn(plant);
        Page<Diary> diaryPage = new PageImpl<>(List.of(Diary.builder().id(diaryId).build()), pageable, size);
        when(diaryRepository.findAllByPlantIdAndDeletedYnFalse(plantId, pageable)).thenReturn(diaryPage);
        when(diaryMapper.toDto(any())).thenReturn(
                new DiaryResDto(diaryId, diaryTitle, diaryContent, diaryCreatedAt, diaryUpdatedAt)
        );

        // when
        PlantResDto result = plantService.readPlant(userId, plantId, page, size);

        // then
        assertNotNull(result);
        verify(plantRepository).findByIdCustom(plantId);
        verify(diaryRepository).findAllByPlantIdAndDeletedYnFalse(plantId, pageable);
        verify(diaryMapper).toDto(any());
    }

    @Test
    void updatePlant_Success() {
        // given
        Long id = 1L;
        Long userId = 1L;
        PlantReqDto plantReqDto = new PlantReqDto("식물이름", 1L, LocalDate.MIN);

        Plant plant = Plant.builder()
                .id(id)
                .userId(userId)
                .species(Species.builder().id(1L).speciesName("종이름").build())
                .build();

        // Mock 설정
        when(plantRepository.findByIdCustom(id)).thenReturn(plant);

        // when
        PlantResDto result = plantService.updatePlant(userId, id, plantReqDto);

        // then
        assertNotNull(result);
        verify(plantRepository).findByIdCustom(id);
        assertEquals(plantReqDto.plantName(), plant.getPlantName());
        assertEquals(plantReqDto.adoptionDate(), plant.getAdoptionDate());
        assertEquals("종이름", result.speciesName());
    }

    @Test
    void deletePlant_Success() {
        //given
        Long id = 1L;
        Plant plant = mock(Plant.class);
        when(plant.getUserId()).thenReturn(userId);

        when(plantRepository.findByIdCustom(id)).thenReturn(plant);

        //when
        plantService.deletePlant(userId, id);

        //then
        verify(plantRepository).findByIdCustom(id);
        verify(plant).getUserId();
        verify(plant).softDelete();
    }

}
