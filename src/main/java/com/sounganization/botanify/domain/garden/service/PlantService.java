package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.dto.res.DiaryResDto;
import com.sounganization.botanify.domain.garden.dto.res.PlantResDto;
import com.sounganization.botanify.domain.garden.entity.Diary;
import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.entity.Species;
import com.sounganization.botanify.domain.garden.repository.DiaryRepository;
import com.sounganization.botanify.domain.garden.repository.PlantRepository;
import com.sounganization.botanify.domain.garden.repository.SpeciesRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.PageRanges;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepository plantRepository;
    private final SpeciesRepository speciesRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public Plant createPlant(String plantName, LocalDate adoptionDate, long speciesId) {

        Species species = speciesRepository.findById(speciesId).orElseThrow(()
                -> new CustomException(ExceptionStatus.SPECIES_NOT_FOUND));

        species.setId(speciesId);
        species.setSpeciesName("임시 종");
        species.setDescription("임시 설명");
        return plantRepository.save(Plant.builder()
                .plantName(plantName)
                .adoptionDate(adoptionDate)
                .species(species)
                .userId(1L)
                .build());
    }

    @Transactional(readOnly = true)
    public PlantResDto getPlant(Long id, int page, int size) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionStatus.PLANT_NOT_FOUND));
        Species species = plant.getSpecies();
        if (species == null) {
            throw new CustomException(ExceptionStatus.SPECIES_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<Diary> diaryPage = diaryRepository.findByPlantIdAndDeletedYnFalse(plant.getId(), pageable);
        List<DiaryResDto> diaries = diaryPage.stream()
                .map(diary -> new DiaryResDto(diary.getTitle(), diary.getContent()))
                .collect(Collectors.toList());

        return new PlantResDto(200, "식물 조회 성공", plant.getId(), plant.getPlantName(), plant.getAdoptionDate(), species.getSpeciesName(), diaries);
    }

    @Transactional
    public void updatePlant(Long id, String plantName, LocalDate adoptionDate) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionStatus.PLANT_NOT_FOUND));

        plant.setPlantName(plantName);
        plant.setAdoptionDate(adoptionDate);
    }

    @Transactional
    public void deletePlant(Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow(() -> new CustomException(ExceptionStatus.PLANT_NOT_FOUND));
        plant.softDelete();
        plantRepository.save(plant);
    }
}