package com.sounganization.botanify.domain.community.controller;

import com.sounganization.botanify.domain.garden.dto.req.PlantReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    @PostMapping
    public ResponseEntity<?> test(@Valid @RequestBody PlantReqDto request) {
        return ResponseEntity.ok().build();
    }
}
