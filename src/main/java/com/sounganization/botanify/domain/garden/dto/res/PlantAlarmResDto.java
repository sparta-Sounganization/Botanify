package com.sounganization.botanify.domain.garden.dto.res;

import com.sounganization.botanify.domain.garden.entity.PlantAlarm;

import java.time.LocalDate;

public record PlantAlarmResDto(
        Long id,
        Long plantId,
        String plantName,
        LocalDate startDate,
        Integer intervalDays,
        Boolean isEnabled,
        PlantAlarm.AlarmType type
) {
}
