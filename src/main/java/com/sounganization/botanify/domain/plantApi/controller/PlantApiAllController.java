package com.sounganization.botanify.domain.plantApi.controller;

import com.sounganization.botanify.domain.plantApi.dto.res.PlantApiResDto;
import com.sounganization.botanify.domain.plantApi.service.PlantApiAllService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PlantApiAllController {
    private final PlantApiAllService apiService;

    //전체 품종의 모든 식물의 상세 정보 조회
    @GetMapping("/api/plant-api/all")
    public Mono<List<PlantApiResDto>> getGrowthForms() {
        return apiService.getSpeciesWithDetails()
                .switchIfEmpty(Mono.just(Collections.emptyList()));
    }
}
