package com.sounganization.botanify.domain.garden.repository;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.entity.Plant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlantRepository extends JpaRepository<Plant, Long> {

    Optional<Plant> findByIdAndDeletedYnFalse(Long id);

    default Plant findByIdCustom(Long id) {
        return this.findByIdAndDeletedYnFalse(id).orElseThrow(() -> new CustomException(ExceptionStatus.PLANT_NOT_FOUND));
    }

    @Query("SELECT p FROM Plant p LEFT JOIN FETCH p.species WHERE p.userId = :userId AND p.deletedYn = false")
    Page<Plant> findAllByUserIdAndDeletedYnFalse(Long userId, Pageable pageable);
}
