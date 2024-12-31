package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.garden.dto.req.PlantAlarmReqDto;
import com.sounganization.botanify.domain.garden.dto.req.PlantAlarmUpdateReqDto;
import com.sounganization.botanify.domain.garden.dto.res.PlantAlarmResDto;
import com.sounganization.botanify.domain.garden.service.PlantAlarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
@RequiredArgsConstructor
public class PlantAlarmController {
    private final PlantAlarmService plantAlarmService;

    @PostMapping("/{plantId}/alarms")
    public ResponseEntity<CommonResDto> createAlarm(
            @PathVariable Long plantId,
            @RequestBody @Valid PlantAlarmReqDto reqDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long alarmId = plantAlarmService.createAlarm(userDetails.getId(), plantId, reqDto);
        return ResponseEntity.ok(new CommonResDto(
                HttpStatus.CREATED,
                "알람이 성공적으로 생성되었습니다.",
                alarmId
        ));
    }

    @GetMapping("/alarms")
    public ResponseEntity<List<PlantAlarmResDto>> getUserAlarms(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<PlantAlarmResDto> alarms = plantAlarmService.getUserAlarms(userDetails.getId());
        return ResponseEntity.ok(alarms);
    }

    @GetMapping("/{plantId}/alarms")
    public ResponseEntity<List<PlantAlarmResDto>> getPlantAlarms(
            @PathVariable Long plantId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<PlantAlarmResDto> alarms = plantAlarmService.getPlantAlarms(userDetails.getId(), plantId);
        return ResponseEntity.ok(alarms);
    }

    @PutMapping("/alarms/{alarmId}")
    public ResponseEntity<CommonResDto> updateAlarm(
            @PathVariable Long alarmId,
            @RequestBody @Valid PlantAlarmUpdateReqDto reqDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        plantAlarmService.updateAlarm(userDetails.getId(), alarmId, reqDto);
        return ResponseEntity.ok(new CommonResDto(
                HttpStatus.OK,
                "알람이 성공적으로 수정되었습니다."
        ));
    }

    @DeleteMapping("/alarms/{alarmId}")
    public ResponseEntity<Void> deleteAlarm(
            @PathVariable Long alarmId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        plantAlarmService.deleteAlarm(userDetails.getId(), alarmId);
        return ResponseEntity.ok().build();
    }
}
