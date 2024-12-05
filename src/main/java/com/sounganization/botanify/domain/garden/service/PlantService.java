package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.domain.garden.dto.PlantRegisterReqDto;
import com.sounganization.botanify.domain.garden.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepository plantRepository;


}
