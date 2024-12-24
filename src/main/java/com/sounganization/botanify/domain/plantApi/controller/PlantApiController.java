package com.sounganization.botanify.domain.plantApi.controller;

import com.sounganization.botanify.domain.plantApi.dto.res.PlantApiResDto;
import com.sounganization.botanify.domain.plantApi.service.PlantApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PlantApiController {
    private final PlantApiService apiService;

    //식물 관리 상세 조회
    @GetMapping("/api/growth-forms")
    public Mono<List<PlantApiResDto>> getGrowthForms() {
        return apiService.getSpeciesWithDetails()
                .switchIfEmpty(Mono.just(Collections.emptyList()));
    }
}
