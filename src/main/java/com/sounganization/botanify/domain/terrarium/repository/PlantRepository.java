package com.sounganization.botanify.domain.terrarium.repository;

import com.sounganization.botanify.domain.terrarium.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantRepository extends JpaRepository<Plant, Long> {
}
