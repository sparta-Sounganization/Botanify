package com.sounganization.botanify.domain.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final WebClient webClient;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.base-url}")
    private String baseUrl;

    @CircuitBreaker(name = "weatherService", fallbackMethod = "fallbackGetCurrentWeather")
    public String getCurrentWeather(String nx, String ny) {
        String baseDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = getClosestBaseTime(LocalDateTime.now());

        // 캐시 키 생성 (nx, ny, baseDate, baseTime 포함)
        String cacheKey = generateCacheKey(nx, ny, baseDate, baseTime);

        // 1. Redis 에서 데이터 조회
        String cachedWeather = redisTemplate.opsForValue().get(cacheKey);
        if (cachedWeather != null) {
            logger.info("Redis Cache Hit for key: {}", cacheKey);
            return cachedWeather;
        }

        // 2. Redis 에 데이터가 없으면 API 호출
        String apiUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/getUltraSrtNcst")
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 10)
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .queryParam("dataType", "JSON")
                .build(false) // 자동 인코딩 방지
                .toUriString();

        String weatherData = webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractWeatherData)
                .block();

        // 3. Redis 에 데이터 저장 (TTL 45분 설정)
        redisTemplate.opsForValue().set(cacheKey, weatherData, 45, TimeUnit.MINUTES);
        logger.info("Redis Cache Set for key: {} with TTL 45 minutes", cacheKey);

        return weatherData;
    }

    private String generateCacheKey(String nx, String ny, String baseDate, String baseTime) {
        // 캐시 키에 nx, ny, 날짜 및 시간 포함
        return String.format("weather:%s:%s:%s%s", nx, ny, baseDate, baseTime);
    }

    private String extractWeatherData(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isMissingNode() || items.isEmpty()) {
                throw new CustomException(ExceptionStatus.NO_WEATHER_DATA);
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
                    default:
                        break;
                }
            }
            return result.toString();
        } catch (Exception e) {
            logger.error("Error parsing weather data: {}", e.getMessage(), e);
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

    @SuppressWarnings("unused")
    private String fallbackGetCurrentWeather(String nx, String ny, Throwable throwable) {
        logger.error("getCurrentWeather 메서드 호출 실패 (nx: {}, ny: {}): {}", nx, ny, throwable.getMessage(), throwable);
        throw new CustomException(ExceptionStatus.WEATHER_SERVICE_NOT_AVAILABLE);
    }
}
