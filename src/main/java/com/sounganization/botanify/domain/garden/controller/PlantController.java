package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.common.exception.GlobalExceptionHandler;
import com.sounganization.botanify.domain.garden.dto.req.PlantReqDto;
import com.sounganization.botanify.domain.garden.dto.res.PlantResDto;
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
    public ResponseEntity<PlantResDto> createPlant(@RequestBody PlantReqDto plantReqDto) {
        Plant createdPlant = plantService.createPlant(plantReqDto.getPlantName(), LocalDate.now(), plantReqDto.getSpeciesId());
        return ResponseEntity.created(null).body(new PlantResDto(201, "식물 등록 성공", createdPlant.getId()));

    }
}
