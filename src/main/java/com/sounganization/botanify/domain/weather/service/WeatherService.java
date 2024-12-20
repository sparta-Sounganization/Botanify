package com.sounganization.botanify.domain.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public WeatherService(@Qualifier("weatherRestTemplate") RestTemplate restTemplate,
                          @Value("${weather.api.key}") String apiKey,
                          @Value("${weather.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public String getCurrentWeather(String nx, String ny) {
        String baseDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = getClosestBaseTime(LocalDateTime.now());

        // UriComponentsBuilder 를 사용하여 URL 생성
        String apiUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/getUltraSrtNcst")
                .queryParam("serviceKey", "{serviceKey}") // 중괄호로 변수화
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 10)
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .queryParam("dataType", "JSON")
                .build(false) // 자동 인코딩 방지
                .expand(apiKey) // {serviceKey} 변수에 apiKey 값 삽입
                .toUriString();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            System.out.println("API Response: " + response.getBody()); // 응답 본문 출력

            return extractWeatherData(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ExceptionStatus.API_INVALID_REQUEST);
        }
    }

    private String extractWeatherData(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isMissingNode() || items.isEmpty()) {
                return "주어진 위치와 시간에 대한 기상 데이터가 없습니다.";
            }

            StringBuilder result = new StringBuilder();
            for (JsonNode item : items) {
                String category = item.path("category").asText();
                String obsrValue = item.path("obsrValue").asText();

                switch (category) {
                    case "T1H":
                        result.append("기온: ").append(obsrValue).append("℃\n");
                        break;
                    case "REH":
                        result.append("습도: ").append(obsrValue).append("%\n");
                        break;
                    case "RN1":
                        result.append("1시간 강수량: ").append(obsrValue).append("mm\n");
                        break;
                    case "WSD":
                        result.append("풍속: ").append(obsrValue).append("m/s\n");
                        break;
                }
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ExceptionStatus.API_DATA_PARSING_ERROR);
        }
    }

    private String getClosestBaseTime(LocalDateTime now) {
        int minute = now.getMinute();
        int hour = now.getHour();

        if (minute < 45) {
            hour -= 1;
        }

        if (hour < 0) {
            hour = 23;
        }

        return String.format("%02d00", hour);
    }
}
