package com.sounganization.botanify.domain.garden.dto.req;

import java.time.LocalDate;

public record PlantAlarmUpdateReqDto(
        LocalDate startDate,
        Integer intervalDays,
        Boolean isEnabled
) {
}
