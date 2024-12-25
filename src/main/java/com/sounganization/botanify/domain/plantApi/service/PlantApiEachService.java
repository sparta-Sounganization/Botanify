package com.sounganization.botanify.domain.plantApi.service;

import com.sounganization.botanify.domain.plantApi.dto.res.CategoryResDto;
import com.sounganization.botanify.domain.plantApi.dto.res.PlantDetailResDto;
import com.sounganization.botanify.domain.plantApi.dto.res.PlantListResDto;
import com.sounganization.botanify.domain.plantApi.util.XmlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import reactor.core.publisher.Mono;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlantApiEachService {

    private final WebClient webClient;
    @Value("${spring.nongsaro.api.key}")
    private String apiKey;

    //품종 코드 조회
    public Mono<List<CategoryResDto>> getSpeciesCategory() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/grwhstleList")
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)  // XML 응답을 문자열로 받음
                .map(this::parseXmlToCategoryResDtoList);  // XML을 CategoryResDto 리스트로 변환
    }

    private List<CategoryResDto> parseXmlToCategoryResDtoList(String xmlResponse) {
        List<CategoryResDto> categoryList = new ArrayList<>();
        try {
            Document document = XmlUtils.parseXml(xmlResponse);
            NodeList itemList = XmlUtils.getNodesByTagName(document, "item");

            for (int i = 0; i < itemList.getLength(); i++) {
                Node itemNode = itemList.item(i);
                String categoryCode = XmlUtils.extractValue(itemNode, "code");
                String categoryName = XmlUtils.extractValue(itemNode, "codeNm");
                if (categoryCode != null && categoryName != null) {
                    categoryList.add(new CategoryResDto(categoryCode, categoryName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoryList;
    }


    //품종 코드에 대한 식물 리스트 조회
    public Mono<String> getCategoryPlantList(String categoryCode, int pageNo) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/gardenList")
                        .queryParam("apiKey", apiKey)
                        .queryParam("grwhstleChkVal", categoryCode)
                        .queryParam("pageNo", pageNo)
                        .build())
                .retrieve()
                .bodyToMono(String.class);  // String으로 응답 받음
    }


    private List<PlantListResDto> parsePlantList(String xmlResponse) {
        List<PlantListResDto> plantList = new ArrayList<>();

        try {
            Document document = XmlUtils.parseXml(xmlResponse);
            NodeList itemNodes = XmlUtils.getNodesByTagName(document, "item");

            for (int i = 0; i < itemNodes.getLength(); i++) {
                Node itemNode = itemNodes.item(i);
                String plantName = XmlUtils.extractValue(itemNode, "cntntsSj");
                String plantCode = XmlUtils.extractValue(itemNode, "cntntsNo");
                if (plantName != null && plantCode != null) {
                    plantList.add(new PlantListResDto(plantCode, plantName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plantList;
    }

    public Mono<List<PlantListResDto>> getSpeciesForCodeWithDetails(String grwhstleCode) {
        return getCategoryPlantList(grwhstleCode, 1)  // 첫 번째 페이지 조회
                .flatMap(response -> {
                    try {
                        // 첫 번째 페이지에서 totalCount 추출
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        InputStream inputStream = new ByteArrayInputStream(response.getBytes("UTF-8"));
                        Document document = builder.parse(inputStream);

                        // totalCount 추출
                        String totalCountStr = XmlUtils.getTagValue(document.getDocumentElement(), "totalCount");
                        int totalCount = totalCountStr != null ? Integer.parseInt(totalCountStr) : 0;
                        int totalPages = (int) Math.ceil((double) totalCount / 10);  // 페이지 수 계산

                        // 총 페이지 수에 맞춰 각 페이지의 식물 정보를 조회하고 DTO로 반환
                        return retrieveSpeciesPagesWithDetails(grwhstleCode, totalPages);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Mono.error(e);  // 오류 발생 시 Mono 에러 반환
                    }
                });
    }

    private Mono<List<PlantListResDto>> retrieveSpeciesPagesWithDetails(String grwhstleCode, int totalPages) {
        List<Mono<List<PlantListResDto>>> pageRequests = new ArrayList<>();
        for (int pageNo = 1; pageNo <= totalPages; pageNo++) {
            final int currentPageNo = pageNo;
            pageRequests.add(getCategoryPlantList(grwhstleCode, currentPageNo)
                    .flatMap(response -> {
                        // 각 페이지에서 식물 리스트 파싱
                        List<PlantListResDto> plantList = parsePlantList(response);  // XML에서 식물 리스트 파싱
                        return Mono.just(plantList);
                    }));
        }

        // 모든 페이지의 데이터를 병합하여 하나의 리스트로 반환
        return Mono.zip(pageRequests, results -> {
            List<PlantListResDto> allPlants = new ArrayList<>();
            for (Object result : results) {
                List<PlantListResDto> plantList = (List<PlantListResDto>) result;
                allPlants.addAll(plantList);  // 각 페이지에서 가져온 식물 데이터 추가
            }
            return allPlants;  // 모든 페이지의 결과를 합쳐서 반환
        });
    }



// 식물 상세 정보 조회
    public Mono<PlantDetailResDto> getPlantInfo(String cntntsNo) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/gardenDtl")
                        .queryParam("apiKey", apiKey)
                        .queryParam("cntntsNo", cntntsNo)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(xmlResponse -> {
                    // XML 응답을 파싱하여 PlantApiResDto로 변환
                    PlantDetailResDto plantInfo = parsePlantInfo(xmlResponse);
                    return Mono.just(plantInfo);
                });
    }


    public static String defaultIfEmpty(String value, String defaultValue) {
        return (value == null || value.trim().isEmpty()) ? defaultValue : value;
    }

    private PlantDetailResDto parsePlantInfo(String xmlResponse) {
        try {
            Document document = XmlUtils.parseXml(xmlResponse);
            NodeList itemNodes = XmlUtils.getNodesByTagName(document, "item");

            for (int i = 0; i < itemNodes.getLength(); i++) {
                Node itemNode = itemNodes.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;
                    return PlantDetailResDto.builder()
                            .smell(defaultIfEmpty(XmlUtils.getTagValue(itemElement, "smellCodeNm"), "N/A"))
                            .toxicity(defaultIfEmpty(XmlUtils.getTagValue(itemElement, "toxctyInfo"), "N/A"))
                            .managementLevel(defaultIfEmpty(XmlUtils.getTagValue(itemElement, "managelevelCodeNm"), "N/A"))
                            .growthSpeed(defaultIfEmpty(XmlUtils.getTagValue(itemElement, "grwtveCodeNm"), "N/A"))
                            .growthTemperature(defaultIfEmpty(XmlUtils.getTagValue(itemElement, "grwhTpCodeNm"), "N/A"))
                            .winterLowestTemp(defaultIfEmpty(XmlUtils.getTagValue(itemElement, "winterLwetTpCodeNm"), "N/A"))
                            .humidity(defaultIfEmpty(XmlUtils.getTagValue(itemElement, "hdCodeNm"), "N/A"))
                            .fertilizerInfo(defaultIfEmpty(XmlUtils.getTagValue(itemElement, "frtlzrInfo"), "N/A"))
                            .waterSpring(defaultIfEmpty(XmlUtils.getTagValue(itemElement, "watercycleSprngCodeNm"), "N/A"))
                            .waterSummer(defaultIfEmpty(XmlUtils.getTagValue(itemElement, "watercycleSummerCodeNm"), "N/A"))
                            .waterAutumn(defaultIfEmpty(XmlUtils.getTagValue(itemElement, "watercycleAutumnCodeNm"), "N/A"))
                            .waterWinter(defaultIfEmpty(XmlUtils.getTagValue(itemElement, "watercycleWinterCodeNm"), "N/A"))
                            .build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
