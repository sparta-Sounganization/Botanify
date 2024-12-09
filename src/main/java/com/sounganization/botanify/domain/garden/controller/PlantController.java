package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.common.exception.GlobalExceptionHandler;
import com.sounganization.botanify.domain.garden.dto.req.PlantReqDto;
import com.sounganization.botanify.domain.garden.dto.res.PlantResDto;
import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.entity.Species;
import com.sounganization.botanify.domain.garden.service.PlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/plants")
@RequiredArgsConstructor
public class PlantController {

    public final PlantService plantService;

    //식물 등록
    @PostMapping
    public ResponseEntity<PlantResDto> createPlant(@RequestBody PlantReqDto plantReqDto) {
        Plant createdPlant = plantService.createPlant(plantReqDto.getPlantName(), LocalDate.now(), plantReqDto.getSpeciesId());
        Species species = createdPlant.getSpecies();
        return ResponseEntity.created(null).body(new PlantResDto(
                201,
                "식물 등록 성공",
                createdPlant.getId(),
                createdPlant.getPlantName(),
                createdPlant.getAdoptionDate(),
                species.getSpeciesName(),
                List.of()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantResDto> getPlant(@PathVariable Long id) {
        PlantResDto plantResDto = plantService.getPlant(id);
        return ResponseEntity.ok(plantResDto);
    }
}
