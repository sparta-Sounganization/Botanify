package com.sounganization.botanify.domain.garden.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public record PlantAlarmUpdateReqDto(

        @NotNull(message = "다음 알람 시간은 필수입니다")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime nextAlarmDateTime,

        @NotNull(message = "선호하는 알람 시간은 필수입니다")
        @DateTimeFormat(pattern = "HH:mm:ss")
        LocalTime preferredTime,

        @NotEmpty(message = "알람 요일을 하나 이상 선택해주세요")
        Set<DayOfWeek> alarmDays,

        @NotNull(message = "알람 활성화 여부는 필수입니다")
        Boolean isEnabled
) {
}
