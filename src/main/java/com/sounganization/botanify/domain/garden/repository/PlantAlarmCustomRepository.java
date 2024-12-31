package com.sounganization.botanify.domain.garden.repository;

import com.sounganization.botanify.domain.garden.entity.PlantAlarm;

import java.time.LocalDateTime;
import java.util.List;

public interface PlantAlarmCustomRepository {
    List<PlantAlarm> findDueAlarms(LocalDateTime currentDateTime);
}
