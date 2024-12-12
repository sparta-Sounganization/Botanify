package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.garden.dto.req.DiaryReqDto;
import com.sounganization.botanify.domain.garden.dto.res.DiaryResDto;
import com.sounganization.botanify.domain.garden.service.DiaryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping("/plants/{plantId}/diaries")
    public ResponseEntity<CommonResDto> createDiary(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletRequest httpReq,
            @PathVariable Long plantId,
            @Valid @RequestBody DiaryReqDto reqDto
    ) {
        CommonResDto resDto = diaryService.createDiary(userDetails.getId(), plantId, reqDto);
        String createdURI = httpReq.getRequestURI() + "/" + resDto.id();
        return ResponseEntity.created(URI.create(createdURI)).body(resDto);
    }

    @GetMapping("/diaries/{id}")
    public ResponseEntity<DiaryResDto> readDiary(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(diaryService.readDiary(userDetails.getId(), id));
    }

    @PutMapping("/diaries/{id}")
    public ResponseEntity<CommonResDto> updateDiary(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @Valid @RequestBody DiaryReqDto reqDto
    ) {
        return ResponseEntity.ok(diaryService.updateDiary(userDetails.getId(), id, reqDto));
    }

    @DeleteMapping("/diaries/{id}")
    public ResponseEntity<Void> deleteDiary(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id
    ) {
        diaryService.deleteDiary(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }
}