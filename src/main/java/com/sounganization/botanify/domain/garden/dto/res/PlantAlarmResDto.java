package com.sounganization.botanify.domain.garden.dto.res;

import com.sounganization.botanify.domain.garden.entity.PlantAlarm;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public record PlantAlarmResDto(
        Long id,
        Long plantId,
        String plantName,
        LocalDateTime nextAlarmDateTime,
        LocalTime preferredTime,
        Set<DayOfWeek> alarmDays,
        Boolean isEnabled,
        PlantAlarm.AlarmType type
) {
}
