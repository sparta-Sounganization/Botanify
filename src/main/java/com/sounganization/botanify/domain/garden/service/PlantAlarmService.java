package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.dto.req.PlantAlarmReqDto;
import com.sounganization.botanify.domain.garden.dto.req.PlantAlarmUpdateReqDto;
import com.sounganization.botanify.domain.garden.dto.res.PlantAlarmResDto;
import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.entity.PlantAlarm;
import com.sounganization.botanify.domain.garden.mapper.PlantAlarmMapper;
import com.sounganization.botanify.domain.garden.repository.PlantAlarmRepository;
import com.sounganization.botanify.domain.garden.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlantAlarmService {
    private final PlantAlarmRepository plantAlarmRepository;
    private final PlantRepository plantRepository;
    private final PlantAlarmMapper plantAlarmMapper;

    @Transactional
    public Long createAlarm(Long userId, Long plantId, PlantAlarmReqDto reqDto) {
        Plant plant = plantRepository.findByIdCustom(plantId);
        if (!Objects.equals(userId, plant.getUserId())) {
            throw new CustomException(ExceptionStatus.PLANT_NOT_OWNED);
        }

        PlantAlarm alarm = plantAlarmMapper.toEntity(reqDto);
        alarm.addPlant(plant);
        alarm.addUserId(userId);

        return plantAlarmRepository.save(alarm).getId();
    }

    @Transactional(readOnly = true)
    public List<PlantAlarmResDto> getUserAlarms(Long userId) {
        return plantAlarmRepository.findByUserIdAndIsEnabledTrue(userId)
                .stream()
                .map(plantAlarmMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlantAlarmResDto> getPlantAlarms(Long userId, Long plantId) {
        Plant plant = plantRepository.findByIdCustom(plantId);
        if (!Objects.equals(userId, plant.getUserId())) {
            throw new CustomException(ExceptionStatus.PLANT_NOT_OWNED);
        }

        return plantAlarmRepository.findByPlantIdAndUserId(plantId, userId)
                .stream()
                .map(plantAlarmMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateAlarm(Long userId, Long alarmId, PlantAlarmUpdateReqDto reqDto) {
        PlantAlarm alarm = plantAlarmRepository.findByIdAndUserId(alarmId, userId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.ALARM_NOT_FOUND));

        alarm.updateSettings(
                reqDto.nextAlarmDateTime(),
                reqDto.preferredTime(),
                reqDto.alarmDays(),
                reqDto.isEnabled()
        );
    }

    @Transactional
    public void deleteAlarm(Long userId, Long alarmId) {
        PlantAlarm alarm = plantAlarmRepository.findByIdAndUserId(alarmId, userId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.ALARM_NOT_FOUND));

        plantAlarmRepository.delete(alarm);
    }
}
