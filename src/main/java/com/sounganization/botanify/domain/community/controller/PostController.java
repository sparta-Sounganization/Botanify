package com.sounganization.botanify.domain.community.controller;

import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.res.PostResDto;
import com.sounganization.botanify.domain.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;

    //게시글 작성
    @PostMapping
    public ResponseEntity<PostResDto> createPost(@Valid @RequestBody PostReqDto postReqDto) {
        Long userId = 1L; //임시 Userid
        PostResDto postResDto = postService.createPost(postReqDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(postResDto);
    }

}
