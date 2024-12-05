package com.sounganization.botanify.domain.terrarium.service;

import com.sounganization.botanify.domain.terrarium.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepository plantRepository;


}
