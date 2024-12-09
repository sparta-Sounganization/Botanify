package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.domain.garden.dto.req.DiaryReqDto;
import com.sounganization.botanify.domain.garden.dto.res.MessageResDto;
import com.sounganization.botanify.domain.garden.entity.Diary;
import com.sounganization.botanify.domain.garden.mapper.DiaryMapper;
import com.sounganization.botanify.domain.garden.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class DiaryControllerV2 {
    private final DiaryService diaryService;

    @PutMapping("/diaries/{id}")
    public ResponseEntity<MessageResDto> updateDiary(
            @RequestParam Long userId,
            @PathVariable Long id,
            @RequestBody DiaryReqDto reqDto)
    {
        Diary reqDiary = DiaryMapper.toEntity(reqDto);

        Diary resDiary = diaryService.updateDiaryLegacy(userId, id, reqDiary);
        Long resId = resDiary.getId();

        return ResponseEntity.ok(DiaryMapper.toUpdatedDto(resId));
    }
}
