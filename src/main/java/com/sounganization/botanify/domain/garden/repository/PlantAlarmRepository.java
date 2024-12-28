package com.sounganization.botanify.domain.garden.repository;

import com.sounganization.botanify.domain.garden.entity.PlantAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlantAlarmRepository extends JpaRepository<PlantAlarm, Long> {
    List<PlantAlarm> findByUserIdAndIsEnabledTrue(Long userId);

    List<PlantAlarm> findByPlantIdAndUserId(Long plantId, Long userId);

    Optional<PlantAlarm> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT pa FROM PlantAlarm pa WHERE pa.isEnabled = true AND pa.startDate <= :today")
    List<PlantAlarm> findActiveAlarms(@Param("today") LocalDate today);

    void deleteByPlantId(Long plantId);
}
