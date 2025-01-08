package com.sounganization.botanify.common.service;

import com.sounganization.botanify.common.config.onesignal.OneSignalClient;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.entity.PlantAlarm;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final OneSignalClient oneSignalClient;
    private final UserRepository userRepository;

    public void sendPlantAlarmNotification(Long userId, String plantName, PlantAlarm.AlarmType alarmType, Long plantId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.USER_NOT_FOUND));

        String title = "Botanify 식물 관리 알림";
        String message = String.format("%s님의 %s의 %s 알림입니다.",
                user.getUsername(),
                plantName,
                getAlarmTypeDescription(alarmType)
        );

        // Web 알림 전송
        try {
            Map<String, Object> webData = new HashMap<>();
            webData.put("redirectUrl", "/plants/" + plantId);
            oneSignalClient.sendNotification(title, message, userId.toString(), "web", webData);
        } catch (Exception e) {
            log.error("웹 알림 전송 실패 - 사용자: {}, 식물: {}, 오류: {}",
                    user.getUsername(), plantName, e.getMessage());
        }

        // Mobile 알림 전송
        try {
            Map<String, Object> mobileData = new HashMap<>();
            mobileData.put("plantId", plantId);
            mobileData.put("alarmType", alarmType.name());
            mobileData.put("redirectScreen", "PlantDetail");
            mobileData.put("screenParams", Map.of("plantId", plantId));

            oneSignalClient.sendNotification(title, message, userId.toString(), "mobile", mobileData);
        } catch (Exception e) {
            log.error("모바일 알림 전송 실패 - 사용자: {}, 식물: {}, 오류: {}",
                    user.getUsername(), plantName, e.getMessage());
        }
    }

    private String getAlarmTypeDescription(PlantAlarm.AlarmType type) {
        return switch (type) {
            case WATER -> "물 주기";
            case FERTILIZER -> "비료 주기";
            case PESTICIDE -> "살충제 뿌리기";
        };
    }
}
