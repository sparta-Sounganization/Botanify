package com.sounganization.botanify.domain.plantApi.dto.res;

import lombok.Builder;

@Builder
public record PlantDetailResDto(
        String smell,                // 냄새
        String toxicity,             // 독성 정보
        String managementLevel,      // 관리 수준
        String growthSpeed,          // 생장 속도
        String growthTemperature,    // 생육 온도
        String winterLowestTemp,     // 겨울 최저 온도
        String humidity,             // 습도
        String fertilizerInfo,       // 비료 정보
        String waterSpring,          // 봄 물주기
        String waterSummer,          // 여름 물주기
        String waterAutumn,          // 가을 물주기
        String waterWinter           // 겨울 물주기
) {
}
