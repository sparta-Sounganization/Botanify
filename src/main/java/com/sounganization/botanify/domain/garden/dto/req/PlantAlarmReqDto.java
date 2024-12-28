package com.sounganization.botanify.domain.garden.dto.req;

import com.sounganization.botanify.domain.garden.entity.PlantAlarm;

import java.time.LocalDate;

public record PlantAlarmReqDto(
        LocalDate startDate,
        Integer intervalDays,
        Boolean isEnabled,
        PlantAlarm.AlarmType type
) {
}
