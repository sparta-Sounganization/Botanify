package com.sounganization.botanify.utils;

import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.entity.PlantAlarm;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.enums.UserRole;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

public class PlantAlarmTestUtils {

    private PlantAlarmTestUtils() {
    }

    public static List<PlantAlarm> createMixedAlarms(LocalDateTime now) {
        Plant plant1 = Plant.builder().plantName("식물1").build();
        Plant plant2 = Plant.builder().plantName("식물2").build();
        ReflectionTestUtils.setField(plant1, "id", 1L);
        ReflectionTestUtils.setField(plant2, "id", 2L);

        PlantAlarm dueAlarm = PlantAlarm.builder()
                .plant(plant1)
                .userId(1L)
                .type(PlantAlarm.AlarmType.WATER)
                .nextAlarmDateTime(now.minusMinutes(5))
                .preferredTime(now.toLocalTime())
                .alarmDays(EnumSet.allOf(DayOfWeek.class))
                .isEnabled(true)
                .build();

        ReflectionTestUtils.setField(dueAlarm, "id", 1L);

        PlantAlarm notDueAlarm = PlantAlarm.builder()
                .plant(plant2)
                .userId(1L)
                .type(PlantAlarm.AlarmType.WATER)
                .nextAlarmDateTime(now.plusHours(1))
                .preferredTime(now.plusHours(1).toLocalTime())
                .alarmDays(EnumSet.allOf(DayOfWeek.class))
                .isEnabled(true)
                .build();

        ReflectionTestUtils.setField(notDueAlarm, "id", 2L);

        return List.of(dueAlarm, notDueAlarm);
    }

    public static List<PlantAlarm> createValidAlarms(LocalDateTime now) {
        Plant plant1 = Plant.builder().plantName("식물1").build();
        Plant plant2 = Plant.builder().plantName("식물2").build();
        ReflectionTestUtils.setField(plant1, "id", 1L);
        ReflectionTestUtils.setField(plant2, "id", 2L);

        PlantAlarm alarm1 = PlantAlarm.builder()
                .plant(plant1)
                .userId(1L)
                .type(PlantAlarm.AlarmType.WATER)
                .nextAlarmDateTime(now.minusMinutes(5))
                .preferredTime(now.toLocalTime())
                .alarmDays(EnumSet.allOf(DayOfWeek.class))
                .isEnabled(true)
                .build();

        PlantAlarm alarm2 = PlantAlarm.builder()
                .plant(plant2)
                .userId(2L)
                .type(PlantAlarm.AlarmType.FERTILIZER)
                .nextAlarmDateTime(now.minusMinutes(10))
                .preferredTime(now.toLocalTime())
                .alarmDays(EnumSet.allOf(DayOfWeek.class))
                .isEnabled(true)
                .build();

        return List.of(alarm1, alarm2);
    }

    public static User createTestUser(Long userId) {
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
        return user;
    }
}
