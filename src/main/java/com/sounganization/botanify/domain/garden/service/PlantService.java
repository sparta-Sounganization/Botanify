package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.domain.garden.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepository plantRepository;


}
