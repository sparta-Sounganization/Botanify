package com.sounganization.botanify.domain.garden.dto.res;

public record SpeciesDetailResDto(
        String rtnFileUrl,           // 이미지
        String codeNm,               //품종명
        String cntntsSj,             // 식물 이름
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
