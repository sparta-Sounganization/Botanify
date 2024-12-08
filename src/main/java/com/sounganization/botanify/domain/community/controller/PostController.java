package com.sounganization.botanify.domain.community.controller;

import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.res.PageDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostResDto;
import com.sounganization.botanify.domain.community.dto.res.PostWithCommentResDto;
import com.sounganization.botanify.domain.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //게시글 조회 - 다건조회
    @GetMapping
    public ResponseEntity<PageDto<PostListResDto>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageDto<PostListResDto> postListResDto = postService.getPosts(page, size);
        return ResponseEntity.ok(postListResDto);
    }

    //게시글 조회 - 단건조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostWithCommentResDto> getPost(@PathVariable Long postId) {
        PostWithCommentResDto postWithCommentResDto = postService.getPost(postId);
        return ResponseEntity.ok(postWithCommentResDto);
    }

}
