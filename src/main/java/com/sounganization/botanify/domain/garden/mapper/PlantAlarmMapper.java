package com.sounganization.botanify.domain.garden.mapper;

import com.sounganization.botanify.domain.garden.dto.req.PlantAlarmReqDto;
import com.sounganization.botanify.domain.garden.dto.res.PlantAlarmResDto;
import com.sounganization.botanify.domain.garden.entity.PlantAlarm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlantAlarmMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plant", ignore = true)
    @Mapping(target = "userId", ignore = true)
    PlantAlarm toEntity(PlantAlarmReqDto dto);

    @Mapping(source = "plant.id", target = "plantId")
    @Mapping(source = "plant.plantName", target = "plantName")
    @Mapping(source = "nextAlarmDateTime", target = "nextAlarmDateTime")
    @Mapping(source = "preferredTime", target = "preferredTime")
    @Mapping(source = "alarmDays", target = "alarmDays")
    @Mapping(source = "type", target = "type")
    PlantAlarmResDto toDto(PlantAlarm entity);
}
