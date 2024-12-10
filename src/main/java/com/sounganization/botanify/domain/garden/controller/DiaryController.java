package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.domain.garden.dto.req.DiaryReqDto;
import com.sounganization.botanify.domain.garden.dto.res.DiaryResDto;
import com.sounganization.botanify.domain.garden.dto.res.MessageResDto;
import com.sounganization.botanify.domain.garden.service.DiaryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping("/plants/{plantId}/diaries")
    public ResponseEntity<MessageResDto> createDiary(
            @RequestParam Long userId,
            HttpServletRequest httpReq,
            @PathVariable Long plantId,
            @Valid @RequestBody DiaryReqDto reqDto)
    {
        MessageResDto resDto = diaryService.createDiary(userId, plantId, reqDto);
        String createdURI = httpReq.getRequestURI() + "/" + resDto.id();
        return ResponseEntity.created(URI.create(createdURI)).body(resDto);
    }

    @GetMapping("/diaries/{id}")
    public ResponseEntity<DiaryResDto> readDiary(@RequestParam Long userId, @PathVariable Long id) {
        return ResponseEntity.ok(diaryService.readDiary(userId, id));
    }

    @PutMapping("/diaries/{id}")
    public ResponseEntity<MessageResDto> updateDiary(
            @RequestParam Long userId,
            @PathVariable Long id,
            @Valid @RequestBody DiaryReqDto reqDto)
    {
        return ResponseEntity.ok(diaryService.updateDiary(userId, id, reqDto));
    }

    @DeleteMapping("/diaries/{id}")
    public ResponseEntity<Void> deleteDiary(@RequestParam Long userId, @PathVariable Long id) {
        diaryService.deleteDiary(userId, id);
        return ResponseEntity.noContent().build();
    }
}