package com.sounganization.botanify.domain.community.controller;

import com.sounganization.botanify.domain.community.dto.res.PopularPostResDto;
import com.sounganization.botanify.domain.community.service.PopularPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/popular")
@RequiredArgsConstructor
public class PopularPostController {

    private final PopularPostService popularPostService;

    @GetMapping
    public ResponseEntity<List<PopularPostResDto>> getPopularPosts(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<PopularPostResDto> popularPosts = popularPostService.getPopularPosts(limit);
        return ResponseEntity.ok(popularPosts);
    }
}
