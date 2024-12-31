package com.sounganization.botanify.domain.garden.repository;

import com.sounganization.botanify.domain.garden.entity.PlantAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantAlarmRepository extends JpaRepository<PlantAlarm, Long>, PlantAlarmCustomRepository {

    @Query("SELECT pa FROM PlantAlarm pa JOIN FETCH pa.plant WHERE pa.userId = :userId AND pa.isEnabled = true")
    List<PlantAlarm> findByUserIdAndIsEnabledTrue(@Param("userId") Long userId);

    @Query("SELECT pa FROM PlantAlarm pa JOIN FETCH pa.plant WHERE pa.plant.id = :plantId AND pa.userId = :userId")
    List<PlantAlarm> findByPlantIdAndUserId(@Param("plantId") Long plantId, @Param("userId") Long userId);

    Optional<PlantAlarm> findByIdAndUserId(Long id, Long userId);
}
