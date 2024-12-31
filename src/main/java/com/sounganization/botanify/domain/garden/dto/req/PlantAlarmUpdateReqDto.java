package com.sounganization.botanify.domain.garden.dto.req;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public record PlantAlarmUpdateReqDto(
        LocalDateTime nextAlarmDateTime,
        LocalTime preferredTime,
        Set<DayOfWeek> alarmDays,
        Boolean isEnabled
) {
}
