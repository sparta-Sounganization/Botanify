package com.sounganization.botanify.domain.garden.service;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    @Value("${weather.api.base-url}")
    private String baseUrl;

    @Value("${weather.api.key}")
    private String apiKey;
}
