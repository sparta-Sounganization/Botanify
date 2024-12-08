package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.domain.garden.dto.req.DiaryReqDto;
import com.sounganization.botanify.domain.garden.dto.res.DiaryResDto;
import com.sounganization.botanify.domain.garden.dto.res.MessageResDto;
import com.sounganization.botanify.domain.garden.entity.Diary;
import com.sounganization.botanify.domain.garden.mapper.DiaryMapper;
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
        Diary reqDiary = DiaryMapper.toEntity(reqDto);

        Diary resDiary = diaryService.createDiary(userId, plantId, reqDiary);
        Long resId = resDiary.getId();
        String createdURI = httpReq.getRequestURI() + "/" + resId;

        return ResponseEntity.created(URI.create(createdURI)).body(DiaryMapper.toCreatedDto(resId));
    }

    @GetMapping("/diaries/{id}")
    public ResponseEntity<DiaryResDto> readDiary(@RequestParam Long userId, @PathVariable Long id) {
        Diary resDiary = diaryService.readDiary(userId, id);
        return ResponseEntity.ok(DiaryMapper.toDto(resDiary));
    }

    @PutMapping("/diaries/{id}")
    public ResponseEntity<MessageResDto> updateDiary(
            @RequestParam Long userId,
            @PathVariable Long id,
            @RequestBody DiaryReqDto reqDto)
    {
        Diary reqDiary = DiaryMapper.toEntity(reqDto);

        Diary resDiary = diaryService.updateDiary(userId, id, reqDiary);
        Long resId = resDiary.getId();

        return ResponseEntity.ok(DiaryMapper.toUpdatedDto(resId));
    }

    @DeleteMapping("/diaries/{id}")
    public ResponseEntity<Void> deleteDiary(@RequestParam Long userId, @PathVariable Long id) {
        diaryService.deleteDiary(userId, id);
        return ResponseEntity.noContent().build();
    }
}

