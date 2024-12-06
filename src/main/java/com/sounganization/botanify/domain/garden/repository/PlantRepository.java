package com.sounganization.botanify.domain.garden.repository;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlantRepository extends JpaRepository<Plant, Long> {

    Optional<Plant> findByIdAndDeletedYnFalse(Long id);

    default Plant findByIdCustom(Long id) {
        return this.findByIdAndDeletedYnFalse(id).orElseThrow(() -> new CustomException(ExceptionStatus.PLANT_NOT_FOUND));
    }

}
