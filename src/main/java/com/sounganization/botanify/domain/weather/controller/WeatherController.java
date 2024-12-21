package com.sounganization.botanify.domain.weather.controller;

import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/current")
    public String getCurrentWeather(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String nx = userDetails.getNx(); // 사용자의 x 좌표
        String ny = userDetails.getNy(); // 사용자의 y 좌표
        return weatherService.getCurrentWeather(nx, ny);
    }
}
