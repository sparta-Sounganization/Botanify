package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.entity.Species;
import com.sounganization.botanify.domain.garden.repository.PlantRepository;
import com.sounganization.botanify.domain.garden.repository.SpeciesRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepository plantRepository;
    private final SpeciesRepository speciesRepository;

    public Plant createPlant(String plantName, LocalDate adoptionDate, long speciesId) {
        //임시 Species 객체 생성
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
}
