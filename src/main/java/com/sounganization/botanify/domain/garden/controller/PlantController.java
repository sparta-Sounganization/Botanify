package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.domain.garden.dto.req.PlantReqDto;
import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.entity.Species;
import com.sounganization.botanify.domain.garden.service.PlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/plants")
@RequiredArgsConstructor
public class PlantController {

    public final PlantService plantService;

    //식물 등록
    @PostMapping
    public ResponseEntity<Plant> createPlant(@RequestBody PlantReqDto plantReqDto) {
        if (plantReqDto.getSpecies() == null) {
            throw new IllegalArgumentException("종을 선택해주세요.");
        } else {
            Plant createdPlant = plantService.createPlant(plantReqDto.getPlantName(), LocalDate.now(), plantReqDto.getSpecies().getId());
            return ResponseEntity.ok(createdPlant);}

    }
}
