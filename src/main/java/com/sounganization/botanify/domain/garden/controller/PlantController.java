package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.garden.dto.req.PlantReqDto;
import com.sounganization.botanify.domain.garden.dto.res.PlantResDto;
import com.sounganization.botanify.domain.garden.service.PlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/plants")
@RequiredArgsConstructor
public class PlantController {

    public final PlantService plantService;

    //식물 등록
    @PostMapping
    public ResponseEntity<CommonResDto> createPlant(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody PlantReqDto plantReqDto) {
        Long createdId = plantService.createPlant(userDetails.getId(), plantReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommonResDto(HttpStatus.CREATED,"식물이 등록되었습니다.",createdId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantResDto> readPlant(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PlantResDto plantResDto = plantService.readPlant(userDetails.getId(), id, page, size);
        return ResponseEntity.ok(plantResDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResDto> updatePlant(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody PlantReqDto plantReqDto) {
        Long updatedId = plantService.updatePlant(userDetails.getId(), id, plantReqDto);
        return ResponseEntity.ok(new CommonResDto(HttpStatus.OK,"식물이 수정되었습니다.",updatedId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        plantService.deletePlant(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
