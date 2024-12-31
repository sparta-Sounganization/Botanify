package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.common.service.NotificationService;
import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.entity.PlantAlarm;
import com.sounganization.botanify.domain.garden.repository.PlantAlarmRepository;
import com.sounganization.botanify.domain.garden.scheduler.PlantAlarmScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static com.sounganization.botanify.utils.PlantAlarmTestUtils.createMixedAlarms;
import static com.sounganization.botanify.utils.PlantAlarmTestUtils.createValidAlarms;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantAlarmSchedulerTest {
    @Mock
    private PlantAlarmRepository plantAlarmRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PlantAlarmScheduler plantAlarmScheduler;

    @Test
    @DisplayName("스케줄러 알림 체크 성공")
    void checkPlantAlarms_Success() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Plant plant = Plant.builder()
                .plantName("테스트 식물")
                .build();

        PlantAlarm alarm = PlantAlarm.builder()
                .plant(plant)
                .userId(1L)
                .type(PlantAlarm.AlarmType.WATER)
                .nextAlarmDateTime(now.minusMinutes(5))
                .preferredTime(now.toLocalTime())
                .alarmDays(EnumSet.allOf(DayOfWeek.class))
                .isEnabled(true)
                .build();

        ReflectionTestUtils.setField(plant, "id", 1L);
        ReflectionTestUtils.setField(alarm, "id", 1L);

        when(plantAlarmRepository.findDueAlarms(any())).thenReturn(List.of(alarm));
        doNothing().when(notificationService).sendPlantAlarmNotification(anyLong(), anyString(), any(), anyLong());

        // When
        plantAlarmScheduler.checkPlantAlarms();

        // Then
        verify(plantAlarmRepository).findDueAlarms(any(LocalDateTime.class));
        verify(notificationService).sendPlantAlarmNotification(
                eq(1L),
                eq("테스트 식물"),
                eq(PlantAlarm.AlarmType.WATER),
                eq(1L)
        );
        verify(plantAlarmRepository).save(any(PlantAlarm.class));
    }

    @Test
    @DisplayName("스케줄러 알림 체크 중 예외 발생")
    void checkPlantAlarms_Exception() {
        // Given
        when(plantAlarmRepository.findDueAlarms(any()))
                .thenThrow(new RuntimeException("Database error"));

        // When
        plantAlarmScheduler.checkPlantAlarms();

        // Then
        verify(plantAlarmRepository).findDueAlarms(any(LocalDateTime.class));
        verify(notificationService, never()).sendPlantAlarmNotification(
                anyLong(),
                anyString(),
                any(PlantAlarm.AlarmType.class),
                anyLong()
        );
    }

    @Test
    @DisplayName("비활성화된 알람은 알림을 보내지 않음")
    void checkPlantAlarms_DisabledAlarm() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Plant plant = Plant.builder()
                .plantName("테스트 식물")
                .build();

        PlantAlarm alarm = PlantAlarm.builder()
                .plant(plant)
                .userId(1L)
                .type(PlantAlarm.AlarmType.WATER)
                .nextAlarmDateTime(now.minusMinutes(5))
                .preferredTime(now.toLocalTime())
                .alarmDays(EnumSet.allOf(DayOfWeek.class))
                .isEnabled(false)
                .build();

        ReflectionTestUtils.setField(plant, "id", 1L);
        ReflectionTestUtils.setField(alarm, "id", 1L);

        when(plantAlarmRepository.findDueAlarms(any())).thenReturn(List.of(alarm));

        // When
        plantAlarmScheduler.checkPlantAlarms();

        // Then
        verify(notificationService, never()).sendPlantAlarmNotification(
                anyLong(),
                anyString(),
                any(PlantAlarm.AlarmType.class),
                anyLong()
        );
    }

    @Test
    @DisplayName("알람 간격이 맞지 않으면 알림을 보내지 않음")
    void checkPlantAlarms_NotDueYet() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Plant plant = Plant.builder()
                .id(1L)
                .plantName("테스트 식물")
                .build();

        PlantAlarm alarm = PlantAlarm.builder()
                .id(1L)
                .plant(plant)
                .userId(1L)
                .type(PlantAlarm.AlarmType.WATER)
                .nextAlarmDateTime(now.minusMinutes(5))
                .preferredTime(now.toLocalTime())
                .alarmDays(EnumSet.allOf(DayOfWeek.class))
                .isEnabled(false)
                .build();

        when(plantAlarmRepository.findDueAlarms(any())).thenReturn(List.of(alarm));

        // When
        plantAlarmScheduler.checkPlantAlarms();

        // Then
        verify(notificationService, never()).sendPlantAlarmNotification(
                anyLong(),
                anyString(),
                any(PlantAlarm.AlarmType.class),
                anyLong()
        );
    }

    @Test
    @DisplayName("여러 알람 중 일부만 전송 시간이 된 경우")
    void checkPlantAlarms_PartialDueAlarms() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        List<PlantAlarm> alarms = createMixedAlarms(now);
        when(plantAlarmRepository.findDueAlarms(any())).thenReturn(alarms);

        // When
        plantAlarmScheduler.checkPlantAlarms();

        // Then
        verify(notificationService, times(1)).sendPlantAlarmNotification(
                eq(1L),
                eq("식물1"),
                eq(PlantAlarm.AlarmType.WATER),
                eq(1L)
        );

        verify(notificationService, never()).sendPlantAlarmNotification(
                eq(2L),
                eq("식물2"),
                any(PlantAlarm.AlarmType.class),
                eq(2L)
        );
    }

    @Test
    @DisplayName("알림 전송 중 일부 실패하는 경우")
    void checkPlantAlarms_PartialNotificationFailure() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        List<PlantAlarm> alarms = createValidAlarms(now);
        when(plantAlarmRepository.findDueAlarms(any())).thenReturn(alarms);

        doNothing()
                .doThrow(new CustomException(ExceptionStatus.NOTIFICATION_SEND_FAILED))
                .when(notificationService)
                .sendPlantAlarmNotification(anyLong(), anyString(), any(), anyLong());

        // When
        plantAlarmScheduler.checkPlantAlarms();

        // Then
        verify(notificationService, times(2)).sendPlantAlarmNotification(
                anyLong(), anyString(), any(PlantAlarm.AlarmType.class), anyLong()
        );
    }
}
