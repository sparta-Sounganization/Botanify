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

import java.time.LocalDate;
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
        LocalDate today = LocalDate.now();
        Plant plant = Plant.builder()
                .plantName("테스트 식물")
                .build();

        PlantAlarm alarm = PlantAlarm.builder()
                .plant(plant)
                .userId(1L)
                .type(PlantAlarm.AlarmType.WATER)
                .startDate(today.minusDays(7))
                .intervalDays(7)
                .isEnabled(true)
                .build();

        ReflectionTestUtils.setField(plant, "id", 1L);
        ReflectionTestUtils.setField(alarm, "id", 1L);

        when(plantAlarmRepository.findDueAlarms(any())).thenReturn(List.of(alarm));
        doNothing().when(notificationService).sendPlantAlarmNotification(anyLong(), anyString(), any(), anyLong());

        // When
        plantAlarmScheduler.checkPlantAlarms();

        // Then
        verify(plantAlarmRepository).findDueAlarms(today);
        verify(notificationService).sendPlantAlarmNotification(
                eq(1L),
                eq("테스트 식물"),
                eq(PlantAlarm.AlarmType.WATER),
                eq(1L)
        );
    }

    @Test
    @DisplayName("스케줄러 알림 체크 중 예외 발생")
    void checkPlantAlarms_Exception() {
        // Given
        LocalDate today = LocalDate.now();
        when(plantAlarmRepository.findDueAlarms(any()))
                .thenThrow(new RuntimeException("Database error"));

        // When
        plantAlarmScheduler.checkPlantAlarms();

        // Then
        verify(plantAlarmRepository).findDueAlarms(today);
        verify(notificationService, never()).sendPlantAlarmNotification(
                anyLong(),
                anyString(),
                any(PlantAlarm.AlarmType.class),
                eq(1L)
        );
    }

    @Test
    @DisplayName("비활성화된 알람은 알림을 보내지 않음")
    void checkPlantAlarms_DisabledAlarm() {
        // Given
        LocalDate today = LocalDate.now();
        Plant plant = Plant.builder()
                .plantName("테스트 식물")
                .build();

        PlantAlarm alarm = PlantAlarm.builder()
                .plant(plant)
                .userId(1L)
                .type(PlantAlarm.AlarmType.WATER)
                .startDate(today.minusDays(7))
                .intervalDays(7)
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
        LocalDate today = LocalDate.now();
        Plant plant = Plant.builder()
                .id(1L)
                .plantName("테스트 식물")
                .build();

        PlantAlarm alarm = PlantAlarm.builder()
                .id(1L)
                .plant(plant)
                .userId(1L)
                .type(PlantAlarm.AlarmType.WATER)
                .startDate(today.minusDays(6))  // 7일 간격인데 6일만 지남
                .intervalDays(7)
                .isEnabled(true)
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
        LocalDate today = LocalDate.now();
        List<PlantAlarm> alarms = createMixedAlarms(today);
        when(plantAlarmRepository.findDueAlarms(any())).thenReturn(alarms);

        // When
        plantAlarmScheduler.checkPlantAlarms();

        // Then
        verify(notificationService, times(1)).sendPlantAlarmNotification(
                anyLong(), anyString(), any(PlantAlarm.AlarmType.class), anyLong()
        );
    }

    @Test
    @DisplayName("알림 전송 중 일부 실패하는 경우")
    void checkPlantAlarms_PartialNotificationFailure() {
        // Given
        LocalDate today = LocalDate.now();
        List<PlantAlarm> alarms = createValidAlarms(today);
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
