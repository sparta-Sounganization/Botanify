package com.sounganization.botanify.domain.garden.repository;

import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    Optional<Plant> findById(Long id);
}
