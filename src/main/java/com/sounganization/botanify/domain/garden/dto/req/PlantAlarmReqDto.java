package com.sounganization.botanify.domain.garden.dto.req;

import com.sounganization.botanify.domain.garden.entity.PlantAlarm;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public record PlantAlarmReqDto(
        LocalDateTime nextAlarmDateTime,
        LocalTime preferredTime,
        Set<DayOfWeek> alarmDays,
        Boolean isEnabled,
        PlantAlarm.AlarmType type
) {
}
