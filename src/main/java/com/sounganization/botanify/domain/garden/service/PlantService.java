package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepository plantRepository;
    private final SpeciesRepository speciesRepository;
    private final DiaryRepository diaryRepository;
    private final PlantMapper plantMapper;
    private final DiaryMapper diaryMapper;

    @Transactional
    public Long createPlant(Long userId, PlantReqDto plantReqDto) {

        Species species = speciesRepository.findByIdCustom(plantReqDto.speciesId());

        Plant plant = plantMapper.toEntity(plantReqDto);
        plant.addRelations(species, userId);

        return plantRepository.save(plant).getId();
    }

    @Transactional(readOnly = true)
    public PlantResDto readPlant(Long userId, Long id, int page, int size) {

        Plant plant = plantRepository.findByIdCustom(id);
        if(!Objects.equals(userId, plant.getUserId())) throw new CustomException(ExceptionStatus.PLANT_NOT_OWNED);

        Species species = plant.getSpecies();
        if (Objects.isNull(species)) {
            throw new CustomException(ExceptionStatus.SPECIES_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Diary> diaryPage = diaryRepository.findAllByPlantIdAndDeletedYnFalse(id, pageable);

        Page<DiaryResDto> diaries = diaryPage.map(diaryMapper::toDto);

        return new PlantResDto(plant.getId(), plant.getPlantName(), plant.getAdoptionDate(), species.getSpeciesName(), diaries);
    }

    @Transactional
    public Long updatePlant(Long userId, Long id, PlantReqDto reqDto) {
        // 식물 찾아와서 소유권 확인
        Plant plant = plantRepository.findByIdCustom(id);
        if(!Objects.equals(userId, plant.getUserId())) throw new CustomException(ExceptionStatus.PLANT_NOT_OWNED);

        plant.update(reqDto.plantName(), reqDto.adoptionDate());

        return plant.getId();
    }

    @Transactional
    public void deletePlant(Long userId, Long id) {
        Plant plant = plantRepository.findByIdCustom(id);
        if(!Objects.equals(userId, plant.getUserId())) throw new CustomException(ExceptionStatus.PLANT_NOT_OWNED);
        plant.softDelete();
    }
}