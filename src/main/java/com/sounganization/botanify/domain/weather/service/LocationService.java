package com.sounganization.botanify.domain.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LocationService {

    private final RestTemplate restTemplate; // Qualifier 사용을 위해 생성자 주입
    private final String apiKey;
    private final String baseUrl;

    public LocationService(@Qualifier("locationRestTemplate") RestTemplate restTemplate,
                           @Value("${kakao.api.key}") String apiKey,
                           @Value("${kakao.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public String[] getCoordinates(String city, String town) {
        String address = city + " " + town;
        String url = String.format("%s?query=%s", baseUrl, address);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode documents = root.path("documents");

            if (documents.isEmpty()) {
                return null;
            }

            String longitude = documents.get(0).path("address").path("x").asText();
            String latitude = documents.get(0).path("address").path("y").asText();

            // 위경도를 격자 좌표로 변환
            return GridService.convertToGrid(Double.parseDouble(longitude), Double.parseDouble(latitude));
        } catch (Exception e) {
            throw new CustomException(ExceptionStatus.API_INVALID_REQUEST);
        }
    }
}
