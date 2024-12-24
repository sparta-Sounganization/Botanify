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
            return convertToGrid(Double.parseDouble(longitude), Double.parseDouble(latitude));
        } catch (Exception e) {
            throw new CustomException(ExceptionStatus.API_INVALID_REQUEST);
        }
    }

    // 위경도를 격자 좌표로 변환하는 메서드
    private String[] convertToGrid(double lon, double lat) {
        double RE = 6371.00877;    // 지구 반경(km)
        double GRID = 5.0;         // 격자 간격(km)
        double SLAT1 = 30.0;       // 표준 위도1
        double SLAT2 = 60.0;       // 표준 위도2
        double OLON = 126.0;       // 기준점 경도
        double OLAT = 38.0;        // 기준점 위도
        double XO = 43;            // 기준점 X좌표
        double YO = 136;           // 기준점 Y좌표

        double DEGRAD = Math.PI / 180.0;
        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = lon * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int x = (int) (ra * Math.sin(theta) + XO + 0.5);
        int y = (int) (ro - ra * Math.cos(theta) + YO + 0.5);

        return new String[]{String.valueOf(x), String.valueOf(y)};
    }
}
