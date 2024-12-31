package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.common.config.onesignal.OneSignalClient;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.common.service.NotificationService;
import com.sounganization.botanify.domain.garden.entity.PlantAlarm;
import com.sounganization.botanify.domain.garden.repository.PlantAlarmRepository;
import com.sounganization.botanify.domain.garden.scheduler.PlantAlarmScheduler;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.enums.UserRole;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.sounganization.botanify.utils.PlantAlarmTestUtils.createTestUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    private OneSignalClient oneSignalClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlantAlarmRepository plantAlarmRepository;

    @InjectMocks
    private NotificationService notificationService;

    @InjectMocks
    private PlantAlarmScheduler plantAlarmScheduler;

    @Test
    @DisplayName("식물 알림 전송 성공 (웹 및 모바일)")
    void sendPlantAlarmNotification_Success() {
        // Given
        Long userId = 1L;
        String plantName = "테스트 식물";
        Long plantId = 1L;
        PlantAlarm.AlarmType alarmType = PlantAlarm.AlarmType.WATER;

        User user = User.builder()
                .email("test@email.com")
                .username("testUser")
                .password("password")
                .role(UserRole.USER)
                .city("Seoul")
                .town("Gangnam")
                .address("123 Test Street")
                .nx("1")
                .ny("1")
                .build();

        ReflectionTestUtils.setField(user, "id", userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(oneSignalClient).sendNotification(anyString(), anyString(), anyString(), anyString(), anyMap());

        // When
        notificationService.sendPlantAlarmNotification(userId, plantName, alarmType, plantId);

        // Then
        verify(oneSignalClient).sendNotification(
                eq("Botanify 식물 관리 알림"),
                eq("testUser님의 테스트 식물의 물 주기 알림입니다."),
                eq("1"),
                eq("web"),
                argThat(map -> map.get("redirectUrl").equals("/plants/1"))
        );

        verify(oneSignalClient).sendNotification(
                eq("Botanify 식물 관리 알림"),
                eq("testUser님의 테스트 식물의 물 주기 알림입니다."),
                eq("1"),
                eq("mobile"),
                argThat(map -> map.get("plantId").equals(plantId)
                        && map.get("alarmType").equals("WATER")
                        && map.get("redirectScreen").equals("PlantDetail"))
        );
    }

    @Test
    @DisplayName("사용자를 찾을 수 없을 때 예외 발생")
    void sendPlantAlarmNotification_UserNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CustomException.class, () ->
                notificationService.sendPlantAlarmNotification(userId, "테스트 식물", PlantAlarm.AlarmType.WATER, 1L)
        );
    }


    @Test
    @DisplayName("웹 알림 전송 실패 시 모바일 알림은 계속 진행")
    void sendPlantAlarmNotification_WebFailureContinuesMobile() {
        // Given
        Long userId = 1L;
        String plantName = "테스트 식물";
        Long plantId = 1L;
        PlantAlarm.AlarmType alarmType = PlantAlarm.AlarmType.WATER;

        User user = User.builder()
                .email("test@email.com")
                .username("testUser")
                .password("password")
                .role(UserRole.USER)
                .city("Seoul")
                .town("Gangnam")
                .address("123 Test Street")
                .nx("1")
                .ny("1")
                .build();

        ReflectionTestUtils.setField(user, "id", userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doThrow(new CustomException(ExceptionStatus.NOTIFICATION_SEND_FAILED))
                .when(oneSignalClient)
                .sendNotification(anyString(), anyString(), anyString(), eq("web"), anyMap());
        doNothing()
                .when(oneSignalClient)
                .sendNotification(anyString(), anyString(), anyString(), eq("mobile"), anyMap());

        // When
        notificationService.sendPlantAlarmNotification(userId, plantName, alarmType, plantId);

        // Then
        verify(oneSignalClient).sendNotification(
                anyString(), anyString(), anyString(), eq("mobile"), anyMap()
        );
    }

    @Test
    @DisplayName("모바일 알림 전송 실패 시 웹 알림은 계속 진행")
    void sendPlantAlarmNotification_MobileFailureContinuesWeb() {
        // Given
        Long userId = 1L;
        String plantName = "테스트 식물";
        Long plantId = 1L;
        PlantAlarm.AlarmType alarmType = PlantAlarm.AlarmType.WATER;

        User user = createTestUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Web succeeds, Mobile fails
        doNothing()
                .when(oneSignalClient)
                .sendNotification(anyString(), anyString(), anyString(), eq("web"), anyMap());
        doThrow(new CustomException(ExceptionStatus.NOTIFICATION_SEND_FAILED))
                .when(oneSignalClient)
                .sendNotification(anyString(), anyString(), anyString(), eq("mobile"), anyMap());

        // When
        notificationService.sendPlantAlarmNotification(userId, plantName, alarmType, plantId);

        // Then
        verify(oneSignalClient).sendNotification(
                anyString(), anyString(), anyString(), eq("web"), anyMap()
        );
    }

    @Test
    @DisplayName("웹과 모바일 모두 전송 실패시 예외 처리")
    void sendPlantAlarmNotification_BothPlatformsFail() {
        // Given
        Long userId = 1L;
        String plantName = "테스트 식물";
        Long plantId = 1L;
        PlantAlarm.AlarmType alarmType = PlantAlarm.AlarmType.WATER;

        User user = createTestUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        doThrow(new CustomException(ExceptionStatus.NOTIFICATION_SEND_FAILED))
                .when(oneSignalClient)
                .sendNotification(anyString(), anyString(), anyString(), anyString(), anyMap());

        // When
        notificationService.sendPlantAlarmNotification(userId, plantName, alarmType, plantId);

        // Then
        verify(oneSignalClient, times(2)).sendNotification(
                anyString(), anyString(), anyString(), anyString(), anyMap()
        );
    }
}
