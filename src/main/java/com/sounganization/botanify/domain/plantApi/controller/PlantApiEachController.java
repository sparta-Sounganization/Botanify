package com.sounganization.botanify.domain.plantApi.controller;

import com.sounganization.botanify.domain.plantApi.dto.res.CategoryResDto;
import com.sounganization.botanify.domain.plantApi.dto.res.PlantDetailResDto;
import com.sounganization.botanify.domain.plantApi.dto.res.PlantListResDto;
import com.sounganization.botanify.domain.plantApi.service.PlantApiEachService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PlantApiEachController {

    /*
     * 1. 사용자가 카테고리 조회  -> (카테고리 검색 -> 품종 코드 조회)
     * 2. 카테고리 선택   -> 카테고리 해당하는 식물 조회 (식물 리스트 조회)
     * 3. 리스트에서 식물 선택 -> (식물 상세 정보 조회)
     */

    @Value("${spring.nongsaro.api.key}")
    private String apiKey;

    private final PlantApiEachService tempService;

    //식물 코드 조회
    @GetMapping("/api/plant-api/species")
    public Mono<List<CategoryResDto>> getSpeciesCategory() {
        return tempService.getSpeciesCategory();
    }

    //식물 코드별 식물 리스트 조회
    @GetMapping("/api/plant-api/species/plantList/{speciesCode}")
    public Mono<List<PlantListResDto>> getCategoryPlantList(@PathVariable String speciesCode) {
        return tempService.getSpeciesForCodeWithDetails(speciesCode);  // 서비스 메서드 호출
    }

    //식물 상세 정보 조회
    @GetMapping("/api/plant-api/species/plantList/detail/{plantcode}")
    public Mono<PlantDetailResDto> getPlantInfo(@PathVariable String plantcode) {
        return tempService.getPlantInfo(plantcode);
    }
}
