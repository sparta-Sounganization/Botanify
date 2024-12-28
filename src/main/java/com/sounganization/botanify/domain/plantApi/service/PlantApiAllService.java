package com.sounganization.botanify.domain.plantApi.service;

import com.sounganization.botanify.domain.plantApi.dto.res.PlantApiResDto;
import com.sounganization.botanify.domain.plantApi.util.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.sounganization.botanify.domain.plantApi.util.XmlUtils.parseSpeciesDetail;

@Slf4j
@Service
public class PlantApiAllService {

    private final WebClient plantWebClient;
    public PlantApiAllService(@Qualifier("plantWebClient") final WebClient plantWebClient) {
        this.plantWebClient = plantWebClient;
    }

    @Value("${nongsaro.api.key}")
    private String apiKey;

    // 품종 코드 조회
    public Mono<List<String>> getSpeciesCode() {
        return plantWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/grwhstleList")
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    Document document = XmlUtils.parseXml(response);
                    NodeList items = document.getElementsByTagName("item");
                    return XmlUtils.extractCodesAndNames(items);
                });
    }

    // 품종 코드에 대한 식물 리스트 조회
    public Mono<String> getSpecies(String grwhstleCode, int pageNo) {
        return plantWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/gardenList")
                        .queryParam("apiKey", apiKey)
                        .queryParam("grwhstleChkVal", grwhstleCode)
                        .queryParam("pageNo", pageNo)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    // 식물 상세 정보 조회
    public Mono<String> getSpeciesDetail(String cntntsNo) {
        return plantWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/gardenDtl")
                        .queryParam("apiKey", apiKey)
                        .queryParam("cntntsNo", cntntsNo)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    //품종 코드 조회 및 해당 품종에 대한 상세 정보 조회
    public Mono<List<PlantApiResDto>> getSpeciesWithDetails() {
        return getSpeciesCode()
                .flatMap(grwhstleCodes -> {
                    if (grwhstleCodes != null && !grwhstleCodes.isEmpty()) {
                        List<Mono<List<PlantApiResDto>>> allSpeciesResults = new ArrayList<>();
                        for (String codeAndName : grwhstleCodes) {
                            // code:codeNm 형식으로 분리
                            String[] parts = codeAndName.split(":");
                            String code = parts[0];  // 품종 코드
                            String codeNm = parts[1];  // 품종 이름

                            // getSpeciesForCodeWithDetails에 전달
                            allSpeciesResults.add(getSpeciesForCodeWithDetails(code, codeNm));
                        }

                        return Mono.zip(allSpeciesResults, results -> {
                            List<PlantApiResDto> finalResult = new ArrayList<>();
                            for (Object result : results) {
                                finalResult.addAll((List<PlantApiResDto>) result);
                            }

                            // 식물 코드 기준으로 중복을 검사하고, 중복되는 두 데이터를 생육 형태만 병합하여 하나로 만든다.
                            finalResult = new ArrayList<>(finalResult.stream().collect(Collectors.toMap(
                                    PlantApiResDto::cntntsNo, res -> res,
                                    (r1, r2) -> PlantApiResDto.builder()
                                            .codeNm(String.format("%s,%s", r1.codeNm(), r2.codeNm()))   // 생육형태만 쉼표로 이어붙여서 새 Dto 로 병합
                                            .cntntsNo(r1.cntntsNo())
                                            .cntntsSj(r1.cntntsSj())
                                            .smell(r1.smell())
                                            .toxicity(r1.toxicity())
                                            .managementLevel(r1.managementLevel())
                                            .growthSpeed(r1.growthSpeed())
                                            .growthTemperature(r1.growthTemperature())
                                            .winterLowestTemp(r1.winterLowestTemp())
                                            .humidity(r1.humidity())
                                            .fertilizerInfo(r1.fertilizerInfo())
                                            .waterSpring(r1.waterSpring())
                                            .waterSummer(r1.waterSummer())
                                            .waterAutumn(r1.waterAutumn())
                                            .waterWinter(r1.waterWinter())
                                            .rtnFileUrl(r1.rtnFileUrl())
                                            .build()
                            )).values());

                            return finalResult;
                        });
                    } else {
                        return Mono.just(Collections.emptyList());
                    }
                });
    }


    //품종별 식물 수, 페이지 수 계산
    private Mono<List<PlantApiResDto>> getSpeciesForCodeWithDetails(String grwhstleCode, String codeNm) {
        return getSpecies(grwhstleCode, 1) // 첫 페이지 조회
                .flatMap(response -> {
                    Document document = XmlUtils.parseXml(response);
                    // 총 개수 가져오기
                    String totalCountStr = XmlUtils.getTagValue(document, "totalCount");
                    int totalCount = totalCountStr != null ? Integer.parseInt(totalCountStr) : 0;
                    int totalPages = (int) Math.ceil((double) totalCount / 10); // 페이지 수 계산

                    // 총 페이지 수에 맞춰 각 페이지의 식물 정보를 조회하고 DTO로 반환
                    return retrieveSpeciesPagesWithDetails(grwhstleCode, totalPages, codeNm);
                });
    }


    //총 페이지 수에 따라 각 페이지 식물 정보 조회
    private Mono<List<PlantApiResDto>> retrieveSpeciesPagesWithDetails(String grwhstleCode, int totalPages, String codeNm) {
        List<Mono<List<PlantApiResDto>>> pageRequests = new ArrayList<>();
        for (int pageNo = 1; pageNo <= totalPages; pageNo++) {
            final int currentPageNo = pageNo;
            pageRequests.add(getSpecies(grwhstleCode, pageNo)
                    .map(XmlUtils::parseXml)
                    .flatMap(document -> parseSpeciesAndRetrieveDetails(document, codeNm)) // 각 페이지에서 상세 정보를 DTO로 반환
                    .doOnSuccess(result -> {
                        log.info("품종: {}, 조회 완료된 페이지: {}/{}", codeNm, currentPageNo, totalPages);
                    })
                    .doOnError(error -> {
                        log.error("품종: {}, 페이지 {} 조회 중 오류 발생: {}", codeNm, currentPageNo, error.getMessage());
                    })
            );
        }
        return Mono.zip(pageRequests, results -> {
            List<PlantApiResDto> allResults = new ArrayList<>();
            for (Object result : results) {
                allResults.addAll((List<PlantApiResDto>) result);
            }
            return allResults; // 모든 페이지의 결과를 합쳐서 반환
        });
    }

    // 품종 정보와 상세 정보 조회
    private Mono<List<PlantApiResDto>> parseSpeciesAndRetrieveDetails(Document document, String codeNm) {
        List<Mono<PlantApiResDto>> detailRequests = new ArrayList<>();
        NodeList items = document.getElementsByTagName("item");

        for (int i = 0; i < items.getLength(); i++) {
            String cntntsNo = XmlUtils.getTagValue(items.item(i), "cntntsNo");
            String cntntsSj = XmlUtils.getTagValue(items.item(i), "cntntsSj");

            String rtnFileUrl = XmlUtils.getTagValue(items.item(i), "rtnFileUrl");
            String[] rtnFileUrls = rtnFileUrl.split("\\|");
            String firstUrl = rtnFileUrls.length > 0 ? rtnFileUrls[0] : null;

            // 상세 정보를 가져와서 DTO에 설정
            Mono<PlantApiResDto> detailRequest = getSpeciesDetail(cntntsNo)
                    .map(detailResponse -> {
                        Document detailDocument = XmlUtils.parseXml(detailResponse);
                        // DTO 객체를 반환
                        return parseSpeciesDetail(detailDocument, codeNm, cntntsNo, cntntsSj,firstUrl);
                    });

            detailRequests.add(detailRequest);
        }
        return Mono.zip(detailRequests, results -> {
            List<PlantApiResDto> plantApiResDtos = new ArrayList<>();
            for (Object result : results) {
                plantApiResDtos.add((PlantApiResDto) result);
            }
            return plantApiResDtos;
        });
    }
}
