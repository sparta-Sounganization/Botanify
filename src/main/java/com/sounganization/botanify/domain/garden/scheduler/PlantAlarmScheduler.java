package com.sounganization.botanify.domain.garden.scheduler;

import com.sounganization.botanify.common.service.NotificationService;
import com.sounganization.botanify.domain.garden.entity.PlantAlarm;
import com.sounganization.botanify.domain.garden.repository.PlantAlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PlantAlarmScheduler {
    private final PlantAlarmRepository plantAlarmRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시 마다 실행
    public void checkPlantAlarms() {
        log.info("식물 알림 체크 시작");
        LocalDate today = LocalDate.now();

        List<PlantAlarm> dueAlarms;
        try {
            dueAlarms = plantAlarmRepository.findDueAlarms(today);
        } catch (Exception e) {
            log.error("알람 조회 중 오류 발생: {}", e.getMessage());
            return;
        }

        for (PlantAlarm alarm : dueAlarms) {
            try {
                if (alarm.getIsEnabled()) {
                    long daysSinceStart = ChronoUnit.DAYS.between(alarm.getStartDate(), today);
                    if (daysSinceStart % alarm.getIntervalDays() == 0) {
                        notificationService.sendPlantAlarmNotification(
                                alarm.getUserId(),
                                alarm.getPlant().getPlantName(),
                                alarm.getType(),
                                alarm.getPlant().getId()
                        );
                    }
                }
            } catch (Exception e) {
                log.error("알림 처리 실패 - 알람 ID: {}, 오류: {}", alarm.getId(), e.getMessage());
            }
        }
    }
}
