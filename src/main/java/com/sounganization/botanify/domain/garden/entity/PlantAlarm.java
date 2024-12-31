package com.sounganization.botanify.domain.garden.entity;

import com.sounganization.botanify.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlantAlarm extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlarmType type;

    @Column(nullable = false)
    private LocalDateTime nextAlarmDateTime;  // 다음 알람이 울릴 날짜와 시간

    @Column(nullable = false)
    private LocalTime preferredTime;  // 사용자가 선호하는 알람 시간

    @ElementCollection
    @CollectionTable(name = "plant_alarm_days",
            joinColumns = @JoinColumn(name = "alarm_id"))
    @Column(name = "alarm_day")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> alarmDays;  // 알람이 울릴 요일들

    @Column(nullable = false)
    private Boolean isEnabled;

    /**
     * 다음 알람 시간을 계산하여 업데이트
     */
    public void updateNextAlarm() {
        this.nextAlarmDateTime = calculateNextAlarmTime();
    }

    /**
     * 다음 알람 시간 계산
     * 1. 현재 시간 기준으로 다음 알람 시간 계산
     * 2. 오늘의 알람 시간이 이미 지났다면 내일부터 체크
     * 3. 설정된 요일 중 가장 가까운 다음 요일 찾기
     */
    private LocalDateTime calculateNextAlarmTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime candidate = now.with(preferredTime);

        // 오늘의 알람 시간이 이미 지났다면 내일부터 체크
        if (now.isAfter(candidate)) {
            candidate = candidate.plusDays(1);
        }

        // 설정된 요일 중 가장 가까운 다음 요일 찾기
        while (!alarmDays.contains(candidate.getDayOfWeek())) {
            candidate = candidate.plusDays(1);
        }

        return candidate;
    }

    public enum AlarmType {
        WATER("물"),
        FERTILIZER("비료"),
        PESTICIDE("살충제");

        private final String description;

        AlarmType(String description) {
            this.description = description;
        }
    }

    public void addPlant(Plant plant) {
        this.plant = plant;
    }

    public void addUserId(Long userId) {
        this.userId = userId;
    }

    public void updateSettings(
            LocalDateTime nextAlarmDateTime,
            LocalTime preferredTime,
            Set<DayOfWeek> alarmDays,
            Boolean isEnabled
    ) {
        this.nextAlarmDateTime = nextAlarmDateTime;
        this.preferredTime = preferredTime;
        this.alarmDays = alarmDays;
        this.isEnabled = isEnabled;
    }
}
