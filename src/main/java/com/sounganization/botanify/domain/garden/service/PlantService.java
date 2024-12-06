package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepository plantRepository;

    public Plant createPlant(Plant plant) {
        return plantRepository.save(plant);
    }

    public Plant getPlant(Long id) {
        // 이 부분 주의할 것. 아래줄 경고문구 다시 확인하고 고칠 것.
        return plantRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 식물이 존재하지 않습니다."));
    }
}
